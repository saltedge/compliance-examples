require "settingslogic"

class Settings < Settingslogic
  # source "#{Rails.root}/config/application.yml"
  source "#{File.expand_path(File.dirname(__FILE__))}/application.yml"
  namespace ENV["RAILS_ENV"] || ENV["RACK_ENV"] || Rails.env
  # namespace Rails.env
  load!
end

# class Settings < Settingslogic
#   source "#{File.expand_path(File.dirname(__FILE__))}/application.yml" 
#   namespace ENV["RAILS_ENV"] || ENV["RACK_ENV"] || Rails.env
#   load!

#   # def file_path(file)
#   #   "#{Rails.root}/config/#{file}"
#   # end

#   # def public_key
#   #   @public_key_file ||= File.read(file_path(public_key_file))
#   # end

#   # def private_key
#   #   @private_key_file ||= File.read(file_path(private_key_file))
#   # end

# end
