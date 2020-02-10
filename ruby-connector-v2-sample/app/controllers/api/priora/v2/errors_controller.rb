class Api::Priora::V2::ErrorsController < Api::Priora::V2::BaseController
  def create
    Rails.logger.warn "Failed response for #{params.dig(:request, :method)} #{params.dig(:request, :url)}"
    Rails.logger.warn "#{params.dig(:error, :error_class)} #{params.dig(:error, :error_message)}"
    Rails.logger.warn params.as_json

    render priora_ok
  end
end
