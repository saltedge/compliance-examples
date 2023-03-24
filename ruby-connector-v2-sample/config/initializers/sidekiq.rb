require './config/settings.rb' unless defined? Settings

password = Settings.sidekiq[:password].empty? ? "" : ":#{Settings.sidekiq[:password]}@"

options = {
  namespace: Settings.sidekiq[:namespace],
  url:       "redis://#{password}#{Settings.sidekiq[:host]}:#{Settings.sidekiq[:port]}/#{Settings.sidekiq[:database]}"
}

Sidekiq.configure_client { |config| config.redis = options }

Sidekiq.configure_server do |config|
  config.redis = options
end
