require 'settingslogic'

class Settings < Settingslogic
  source "#{File.expand_path(__dir__)}/application.yml"
  namespace ENV["RAILS_ENV"] || ENV["RACK_ENV"] || Rails.env
  load!

  ENVIRONMENTS = %w(development production staging test)

  ENVIRONMENTS.each do |name|
    define_method "#{name}?" do
      Settings.namespace == name
    end
  end

  def file_path(file)
    Rails.root.join('config', file)
  end

  def connector_private_key
    @connector_private_key ||= File.read(file_path(application.private_key_path))
  end

  def priora_public_key
    File.read(file_path(priora.public_key_path))
  end
end
