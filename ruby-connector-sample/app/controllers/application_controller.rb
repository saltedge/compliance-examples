class ApplicationController < ActionController::Base
  protect_from_forgery with: :exception
  after_action -> { flash.clear }

  def homepage
    render json: {}
  end

private

  def clear_params(hash)
    hash     = hash.to_unsafe_h
    resource = self.class.name.demodulize.underscore.sub("_controller", "").singularize
    hash.delete(resource) if hash[resource].is_a?(Hash)
    ActionDispatch::Http::ParameterFilter.new(Rails.application.config.filter_parameters).filter(hash)
  end
end
