require "settingslogic"

class Settings < Settingslogic
  source "#{File.expand_path(File.dirname(__FILE__))}/application.yml"
  namespace ENV["RAILS_ENV"] || ENV["RACK_ENV"] || Rails.env
  load!
end
