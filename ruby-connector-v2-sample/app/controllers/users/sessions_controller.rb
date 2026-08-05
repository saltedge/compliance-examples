class Users::SessionsController < Devise::SessionsController
  # Already-signed-in users are redirected to / before #new/#create, which skips
  # Priora token confirmation and the TPP redirect. Allow the Priora login URL through.
  skip_before_action :require_no_authentication, only: [:new], if: -> { params[:session_secret].present? }

  def new
    @session_secret = params[:session_secret]
    return if user_signed_in? && complete_priora_authorization!(@session_secret)

    super
  end

  def create
    super do |_user|
      return if complete_priora_authorization!(params.dig(:user, :session_secret))
    end
  end

  private

  def complete_priora_authorization!(session_secret)
    return false if session_secret.blank?

    token = Token.find_by(session_secret: session_secret)
    return false unless token

    token.update!(status: Token::CONFIRMED, user_id: current_user.id)
    Callbacks::SessionSuccesser.perform_later(token_id: token.id)
    redirect_to token.extra["redirect_url"]
    true
  end
end
