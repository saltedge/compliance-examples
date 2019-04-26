module ApiErrorHandling
  extend ActiveSupport::Concern

  included do
    rescue_from(StandardError,                                                    with: :handle_internal_server_error)
    rescue_from(Error::ConnectorError,                                            with: :handle_api_exception)
    rescue_from(Encoding::UndefinedConversionError,                               with: :handle_encoding_exception)
    rescue_from(ActionDispatch::Http::Parameters::ParseError, JSON::ParserError,  with: :handle_invalid_json_exception)
    rescue_from(JWT::DecodeError,                                                 with: :handle_jwt_decoding_exception)
    rescue_from(JWT::ExpiredSignature,                                            with: :handle_jwt_signature_exception)
    rescue_from(JWT::VerificationError,                                           with: :handle_jwt_verification_exception)
    rescue_from(JWT::IncorrectAlgorithm,                                          with: :handle_jwt_algorithm_exception)
  end

  def handle_jwt_algorithm_exception
    error = Error::External::JWTIncorrectAlgorithm.new
    handle_api_exception(error)
  end

  def handle_jwt_verification_exception
    error = Error::External::JWTVerificationError.new
    handle_api_exception(error)
  end

  def handle_jwt_decoding_exception
    error = Error::External::JWTDecodeError.new
    handle_api_exception(error)
  end

  def handle_jwt_signature_exception
    error = Error::External::JWTExpiredSignature.new
    handle_api_exception(error)
  end

  def handle_encoding_exception
    error = Error::External::InvalidEncoding.new
    handle_api_exception(error)
  end

  def handle_invalid_json_exception
    error = Error::External::WrongRequestFormat.new
    handle_api_exception(error)
  end

  def handle_pg_range_exception
    error = Error::External::ValueOutOfRange.new
    handle_api_exception(error)
  end

  def handle_internal_server_error(original_exception)
    error = Error::External::InternalServerError.new
    handle_api_exception(error)
  end

  def handle_api_exception(error)
    hash = error.hash_with_params(clear_params(params))
    hash.delete(:status)
    @error_class   = error.class.to_s
    @error_message = error.message
    respond_to do |format|
      format.json { render json: hash.to_json, status: error.status }
    end
  end
end
