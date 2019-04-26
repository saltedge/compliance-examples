require_relative 'boot'

require "rails"
require "active_model/railtie"
require "active_job/railtie"
require "active_record/railtie"
require "action_controller/railtie"
require "action_mailer/railtie"
require "action_view/railtie"
require "sprockets/railtie"
require "pp"

Bundler.require(:default, Rails.env)

module DemoConnector
  class Application < Rails::Application
    require File.expand_path('../settings', __FILE__)

    config.load_defaults 5.2
    config.eager_load_paths += %W(#{config.root}/lib)

    config.filter_parameters += %i(password password_confirmation pin otp)
    config.secret_key_base = Settings.secret_key_base
    config.session_store :cookie_store, key: Settings.session_store

    # Don't generate system test files.
    config.generators.system_tests = nil

    config.action_controller.default_url_options = {host: Settings.application.host}
  end
end
