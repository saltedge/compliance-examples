class Api::BaseController < ApplicationController
  skip_before_action :verify_authenticity_token
  include ApiErrorHandling

private

  def token
    return @token if @token
    return unless request.headers["HTTP_ACCESS_TOKEN"]

    @token = Token.find_by(token: request.headers["HTTP_ACCESS_TOKEN"])

    unless @token
      raise Error::External::TokenNotFound.new(request.headers["HTTP_ACCESS_TOKEN"], 'provider_token')
    end

    unless @token.active?
      raise Error::External::TokenExpired.new(request.headers["HTTP_ACCESS_TOKEN"], 'provider_token')
    end

    @token
  end

  def validate_data_params!(*required_params)
    error = Error::External::WrongRequestFormat
    raise error unless data_params.as_json.is_a?(Hash)
    raise error if data_params.as_json.values_at(*required_params).include? nil
  end

  def data_params
    return @data_params if @data_params

    @data_params = JWT.decode(
      bearer_token,
      OpenSSL::PKey::RSA.new(Settings.priora_public_key),
      true,
      { algorithm: 'RS256' }
    ).first["data"]

    @data_params
  end

  def bearer_token
    if request.authorization && request.authorization.match(/^Bearer \S+/i).present?
      request.authorization.gsub(/^Bearer /i, '')
    else
      raise Error::External::AuthorizationMissing
    end
  end
end
