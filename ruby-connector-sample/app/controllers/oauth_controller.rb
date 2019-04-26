class OauthController < ApplicationController
  before_action :handle_token_presence!, except: %i(grant_access confirm_payment)

  def authorize
    @authorization_fields = Sandbox::Adapter.config.authorization_fields
  end

  def verify_authorization
    errors = Sandbox::Adapter.validate_credentials(
      params.slice(*Sandbox::Adapter.config.authorization_fields.keys)
    )

    if errors
      @authorization_fields = Sandbox::Adapter.config.authorization_fields
      flash[:error] = "Invalid #{errors.keys.join(', ')}"
      return render :authorize
    end

    session[:sandbox_authorized] = true

    if session[:sandbox_mode] == "pisp"
      session.delete(:sandbox_authorized)
      session[:sandbox_session_expires_at] = 5.minutes.from_now
      return redirect_to oauth_confirm_payment_path(payment_id: session[:payment_id])
    end

    redirect_to oauth_otp_path
  end

  def otp
    redirect_back(fallback_location: oauth_verify_otp_path) unless session[:sandbox_authorized]
    @interactive_fields = Sandbox::Adapter.config.interactive_fields
  end

  def verify_otp
    redirect_to oauth_authorize_path unless session[:sandbox_authorized]

    unless Sandbox::Adapter.config.interactive_fields_credentials[:otp] == params[:otp]
      flash[:error] = "Invalid otp"
      @interactive_fields = Sandbox::Adapter.config.interactive_fields
      return render :otp
    end

    session.delete(:sandbox_authorized)
    session[:sandbox_session_expires_at] = 5.minutes.from_now
    render :grant_access
  end

  def grant_access
    save_sandbox_aisp!
  end

  def grant
    process_consent_response!(:success,
      token:            current_token.token,
      token_expires_at: current_token.expires_at
    )
  end

  def deny
    process_consent_response!(:fail,
      error_class:   Error::External::AccessDenied.to_s.demodulize,
      error_message: "Access was denied by the user."
    )
  end

  def confirm_payment
    save_sandbox_pisp!
    payment = Payment.find_by(id: (params["payment_id"] || session[:payment_id]))
    return redirect_to Settings.priora_demo.base_url unless payment
    @attributes = Sandbox::Adapter.payment_confirmation_data(payment)
  end

  def cancel_payment
    payment = Payment.find(params["payment_id"])
    payment.update_attributes!(status: "canceled")
    Connector::Payment.fail(payment.priora_payment_id, {
      "provider_code" => payment.token.provider_code,
      "error_class"   => "SessionClosed",
      "error_message" => I18n.t("error.message.CanceledByCustomer")
    })
    session.delete(:token_id)
    redirect_to URI.parse(payment.extra["redirect_url"])&.to_s
  end

  def make_payment
    payment = Payment.find(params["payment_id"])
    adapter = Sandbox::Adapter.new(current_token)

    data_params = {
      "priora_payment_id" => payment.priora_payment_id,
      "confirmation_code" => params[:credentials][:otp],
      "payment_id"        => payment.id
    }

    adapter.prepare_confirmation({"data_params" => data_params})
    adapter.confirm_payment

    clear_session!
    redirect_to URI.parse(payment.extra["redirect_url"]).to_s
  end

private

  def save_sandbox_aisp!
    session[:sandbox_mode] = "aisp"
    return redirect_to Settings.priora_demo.base_url unless params[:session_secret] || current_token.present?
    session[:token_id] = Token.find_by(session_secret: params[:session_secret])&.id
    return redirect_to oauth_authorize_path unless sandbox_authenticated?
  end

  def save_sandbox_pisp!
    session[:sandbox_mode] = "pisp"
    session[:payment_id]   = params[:payment_id]
    session[:token_id]     = Payment.find_by(id: params["payment_id"])&.token&.id
    return unless session[:token_id]
    return redirect_to oauth_authorize_path unless sandbox_authenticated?
  end

  def process_consent_response!(method, attributes={})
    status = method == :success ? Token::CONFIRMED : Token::REVOKED
    current_token.update_attributes!(status: status, user_id: User.last.id)
    options = {
      "user_id" => User.last.id,
      "provider_code" => current_token.provider_code,
      "extra" => {}
    }.merge(attributes).as_json
    Connector::Session.public_send(method, current_token.session_secret, options)

    clear_session!
    redirect_to current_token.redirect_url
  end

  def current_token
    @current_token ||= Token.find_by(id: session[:token_id])
  end

  def sandbox_authenticated?
    session[:sandbox_session_expires_at] && session[:sandbox_session_expires_at] > Time.now && current_token
  end

  def handle_token_presence!
    return redirect_to Settings.priora_demo.base_url unless current_token
  end

  def clear_session!
    %i(token_id payment_id sandbox_mode).each { |attr| session.delete(attr) }
  end
end
