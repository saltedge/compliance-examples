class ApplicationJob < ActiveJob::Base
  include Rails.application.routes.url_helpers

  queue_as :default

  rescue_from StandardError do |ex|
    Rails.logger.warn error(ex.message)
  end
end
