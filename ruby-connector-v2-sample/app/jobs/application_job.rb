class ApplicationJob < ActiveJob::Base
  include Rails.application.routes.url_helpers

  queue_as :default

  rescue_from StandardError do |ex|
    binding.pry
  end
end
