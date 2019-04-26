module Sandbox
  class Adapter
    extend Config
    include Helper

    attr_reader   :authorization_failed, :payment_failed, :payment
    attr_accessor :token, :session_secret, :callback_params, :confirmation_params, :authorization_params,
      :payment_params, :payment_attributes, :all_params, :app_name, :funds_check_params

    BANK_CURRENCY  = "EUR"
    EXCHANGE_RATES = { "USD" => { BANK_CURRENCY => 0.812 }, "GBP" => { BANK_CURRENCY => 1.502 }, "EUR" => { BANK_CURRENCY => 1.0 } }
    EXCHANGE_RATES.default = { BANK_CURRENCY => 1.0 }

    class << self
      attr_reader :config

      def user
        user = User.find_by(email: "demobankuser@example.com")
        raise StandardError.new("User not found. Run `bundle exec rake db:seed`") unless user
        user
      end

      def payment_confirmation_redirect_url(payment_id)
        if config.payment_confirmation_url
          URI.join(config.payment_confirmation_url, payment_id.to_s)
        end
      end

      def payment_confirmation_data(payment)
        {
          from:        payment.payment_attributes["from_account"],
          to:          payment.payment_attributes["to_account"],
          amount:      payment.payment_attributes["amount"],
          description: payment.description,
          id:          payment.id,
          total:       payment.total
        }
      end

      def validate_credentials(params)
        errors = config.valid_credentials.reject do |key, value|
          value == params[key]
        end
        errors.empty? ? nil : errors
      end

      def oauth_url(session_secret)
        URI.join(Settings.application.url, "oauth/grant_access?session_secret=#{session_secret}")
      end
    end

    def initialize(token=nil)
      @token = token
    end

    def config
      self.class.config
    end

    def self.code
      config.code
    end

    def code
      self.class.code
    end

    def user
      self.class.user
    end

    def oauth_authorize
      create_token
      callback_params["status"] = "redirect"
      callback_params["extra"]["redirect_url"] = self.class.oauth_url(session_secret)
      Connector::Session.update(session_secret, callback_params)
    end

    def prepare_authorization(params)
      @authorization_params = params["data_params"]["original_request"]["client_payload"]["data"]
      @callback_params      = {"extra" =>{}, "provider_code" => code}
      @session_secret       = params["data_params"]["session_secret"]
      @app_name             = params["data_params"]["app_name"]
    end

    def prepare_confirmation(params)
      @all_params          = params # temp fallback
      @confirmation_params = params["data_params"]
      @callback_params     = {"provider_code" => code, "extra" => {}}

      if params["data_params"]["payment_id"]
        @payment = token.user.payments.find_by(id: params["data_params"]["payment_id"])
        @confirmation_params["payment_id"] = params["data_params"]["payment_id"]
        @confirmation_params["confirmation_code"] = params["data_params"]["confirmation_code"]
      end
    end

    def prepare_payment(params)
      data_params         = params["data_params"]["original_request"]["client_payload"]["data"]
      @payment_params     = params["data_params"]
      @callback_params    = {"extra" => {}, "provider_code" => code}
      @payment_attributes = data_params["payment_attributes"]
    end

    def perform_payment
      type                  = payment_params["payment_type"]
      extra                 = payment_params["extra"] || {}
      extra["payment_type"] = type
      extra["redirect_url"] = payment_params["redirect_url"]
      description           = payment_attributes["description"] || type
      account               = Account.find_by(number: payment_attributes["from_account"])

      unless account
        return fail_payment_with(
          error_message: I18n.t("error.message.InvalidPaymentAccount"),
          error_class:   Error::External::InvalidPaymentAccount.to_s.demodulize
        )
      end

      payment = token.payments.create!(
        priora_payment_id:  payment_params["priora_payment_id"],
        account_id:         account.id,
        user_id:            token.user.id,
        extra:              extra,
        payment_attributes: payment_attributes,
        description:        description,
        fees:               {},
        status:             Transaction::PENDING,
        total:              payment_attributes["amount"].to_f
      )
      enrich_callback_params!(payment)
      payment.update_attributes!(confirmation_code: callback_params["confirmation_code"])
      Connector::Payment.update(payment_params["priora_payment_id"], callback_params)
    end

    def confirm_payment
      unless payment
        callback_params["error_message"] = "Payment doesn't exist."
        return fail_payment_with(
          error_message: I18n.t("error.message.UnknownConfirmationFlow"),
          error_class:   Error::External::UnknownConfirmationFlow.to_s.demodulize
        )
      end

      if confirmation_params["confirmation_code"] == payment.confirmation_code
        update_balances
        create_transactions
        payment.update_attributes!(status: "confirmed")
        Connector::Payment.success(payment.priora_payment_id, {"provider_code" => code})
      else
        payment.update_attributes!(status: "failed")
        return fail_payment_with(
          error_message: I18n.t("error.message.InvalidConfirmationCode"),
          error_class:   Error::External::InvalidConfirmationCode.to_s.demodulize
        )
      end
    end

    def prepare_funds_check(params)
      data_params = params[:data_params]
      @session_secret     = data_params["session_secret"]
      @callback_params    = { "provider_code" => code }
      @funds_check_params = {
        amount:        data_params["amount"],
        currency_code: data_params["currency_code"],
        account:       data_params["account"]
      }
    end

    def check_funds_availability
      amount   = funds_check_params[:amount].to_f
      currency = funds_check_params[:currency_code]
      account  = Account.find_by(number: funds_check_params[:account])

      if amount < 0.0
        fail_session_with(Error::External::InvalidAttributeValue.new("amount"))
      elsif EXCHANGE_RATES.keys.exclude?(currency)
        fail_session_with(Error::External::InvalidAttributeValue.new("currency_code"))
      elsif account.nil?
        fail_session_with(Error::External::InvalidPaymentAccount.new)
      else
        converted_amount = amount * EXCHANGE_RATES[currency][BANK_CURRENCY]
        result = account.available_amount.to_f + account.credit_limit.to_f >= converted_amount

        callback_params["funds_available"] = result
        Connector::Session.success(session_secret, callback_params)
      end
    end
  end
end
