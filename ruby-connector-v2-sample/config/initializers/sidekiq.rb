settings = Rails.application.credentials
password = settings.sidekiq[:password].empty? ? "" : ":#{settings.sidekiq[:password]}@"

options = {
  namespace: settings.sidekiq[:namespace],
  url:       "redis://#{password}#{settings.sidekiq[:host]}:#{settings.sidekiq[:port]}/#{settings.sidekiq[:database]}"
}

Sidekiq.configure_client { |config| config.redis = options }

Sidekiq.configure_server do |config|
  config.redis = options
end

Sidekiq::Extensions.enable_delay!

