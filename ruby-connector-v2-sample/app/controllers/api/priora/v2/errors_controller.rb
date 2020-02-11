class Api::Priora::V2::ErrorsController < Api::Priora::V2::BaseController
  def create
    Rails.logger.warn "Failed response for #{jwt_params.dig(:request, :method)} #{jwt_params.dig(:request, :url)}"
    Rails.logger.warn "#{jwt_params.dig(:error, :error_class)} #{jwt_params.dig(:error, :error_message)}"
    Rails.logger.warn jwt_params.as_json

    render priora_ok
  end
end
