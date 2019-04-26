module Sandbox
  module Config
    USER_DATA      = JSON.parse(File.read(Rails.root.join("db", "fixtures", "user.json"))).without("kyc")
    ACCOUNTS_DATA  = JSON.parse(File.read(Rails.root.join("db", "fixtures", "accounts.json")))

    def configure
      config = @config || OpenStruct.new
      yield config

      adapter_class = Sandbox::Adapter.name

      if config.authorization_types.blank?
        abort("#{adapter_class} has no authorization types listed.")
      end

      unless config.code
        abort("#{adapter_class} has no value for 'code' in config.")
      end

      settings = Settings.priora_app
      settings.each { |k, v| config.send("#{k}=".to_sym, v) unless config.send(k.to_sym) } if settings.present?

      ["app_id", "app_secret"].each do |setting|
        unless config.send(setting.to_sym)
          abort("#{adapter_class} has no value for #{setting}.")
        end
      end

      @config = config
    end

    def self.extended(base)
      base.configure do |c|
        c.humanized_name = "Demo Connector Bank"
        c.code = "demobank"
        c.authorization_types = %w(oauth)
        c.authorization_fields = {
          login: {
            humanized_name: "Login",
            html_tag: :text_field_tag
          },
          password: {
            humanized_name: "Password",
            html_tag: :password_field_tag
          }
        }
        c.interactive_fields = {
          otp: {
            humanized_name: "OTP",
            html_tag: :password_field_tag
          }
        }
        c.valid_credentials = {
          login: "demobankuser",
          password: "1234"
        }
        c.interactive_fields_credentials = {otp: "123"}
        c.fixtures_path = Rails.root.join("data", "sandbox", c.code)
        c.payment_confirmation_url = Settings.application.url + "/oauth/confirm_payment/"
        c.payment_confirmation_fields = {
          otp: {
            humanized_name: "OTP",
            html_tag: :password_field_tag
          },
        }
        c.payment_confirmation_credentials = {otp: "456"}
      end
    end
  end
end
