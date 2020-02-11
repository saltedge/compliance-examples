Devise.setup do |config|
  require 'devise/orm/active_record'
  # config.mailer_sender                = Settings.devise.mailer_sender
  # config.mailer.default_url_options   = { host: Settings.application.host }
  config.secret_key                   = Rails.application.credentials.devise[:secret_key]
  config.authentication_keys          = [:email]
  config.case_insensitive_keys        = [:email]
  config.strip_whitespace_keys        = [:email]
  config.skip_session_storage         = [:http_auth]
  config.stretches                    = Rails.env.test? ? 1 : 10
  # config.reconfirmable                = true
  config.remember_for                 = 30.minutes
  config.timeout_in                   = 30.minutes
  config.password_length              = 4..128
  config.scoped_views                 = true
  config.sign_out_via                 = :get
  config.sign_out_all_scopes          = true
  config.paranoid                     = true
  config.sign_in_after_reset_password = false
end
