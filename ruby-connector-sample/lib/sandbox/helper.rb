module Sandbox
  module Helper
    def enrich_callback_params!(payment)
      callback_params["payment_id"] = payment.id
      status, confirmation_code = ["waiting_confirmation", self.config.payment_confirmation_credentials[:otp]]

      callback_params.merge!("status" => status, "confirmation_code" => confirmation_code)

      redirect_url = self.class.payment_confirmation_redirect_url(payment.id)
      return unless redirect_url
      callback_params.merge!({
        "status" => "redirect",
        "extra" => {
          "redirect_url" => redirect_url
        }
      })
    end

    def fail_payment_with(options={})
      callback_params["error_message"] = options[:error_message]
      callback_params["error_class"]   = options[:error_class]

      priora_payment_id = if payment_params
        payment_params["priora_payment_id"]
      else
        all_params["data_params"]["priora_payment_id"]
      end
      Connector::Payment.fail(priora_payment_id, callback_params)
    end

    def update_balances
      @source_account = user.accounts.find(payment.account_id)
      @source_account.update!(
        balance:          @source_account.balance.to_f - payment.total.to_f,
        available_amount: @source_account.available_amount.to_f - payment.total.to_f
      )

      if payment.extra["payment_type"] == "internal_transfer"
        @destination_account = Account.where.not(
          id: payment.account_id
        ).find_by(
          number: payment.payment_attributes["to_account"]
        )
      end

      return unless @destination_account

      amount = payment.payment_attributes["amount"].to_f
      @destination_account.balance = @destination_account.balance + amount
      @destination_account.available_amount = @destination_account.available_amount.to_f + amount
      @destination_account.save!
    end

    def create_transactions
      description = "Payment from #{@source_account.number}"

      amount = payment.payment_attributes["amount"].to_f

      transaction = @source_account.transactions.create!(
        currency_code: @source_account.currency_code,
        description: description,
        made_on: Time.now.utc,
        amount: -amount,
        status: Transaction::POSTED,
        balance: @source_account.balance
      )

      return unless @destination_account

      @destination_account.transactions.create!(
        currency_code: @destination_account.currency_code,
        description: description,
        made_on: Time.now.utc,
        amount: amount,
        status: Transaction::POSTED,
        balance: @destination_account.balance
      )
    end

    def create_token
      @token = Token.create!(
        session_secret:     session_secret,
        scopes:             authorization_params["scopes"],
        public_key:         authorization_params["public_key"],
        authorization_type: "oauth",
        provider_code:      code,
        app_name:           app_name,
        redirect_url:       authorization_params["redirect_url"]
      )
    end
  end
end
