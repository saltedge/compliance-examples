module Error
  class ConnectorError < StandardError
    PARAMS_IGNORE ||= %w(format action controller connection route error).freeze

    def initialize(message=I18n.t("error.message.#{self.class.name.demodulize}"))
      super
    end

    def hash_with_params(params={})
      {
        error_class:   self.class.name.demodulize,
        error_message: message,
        request:       cleanup_params(params),
        status:        status
      }
    end

    def status; :internal_server_error end

  private

    def cleanup_params(params)
      params.reject { |key, value| PARAMS_IGNORE.include?(key) }
    end
  end

  class NotFound < ConnectorError
    def status; :not_found end

    def initialize(value, attribute=nil)
      super I18n.t("error.message.#{self.class.name.demodulize}", attribute: attribute, value: value)
    end
  end

  class Unauthorized < ConnectorError
    def status; :unauthorized end

    def initialize(value, attribute=nil)
      super I18n.t("error.message.#{self.class.name.demodulize}", attribute: attribute, value: value)
    end
  end

  class Invalid < ConnectorError
    def status; :not_acceptable end
  end

  class ServerError < ConnectorError
    def status; :internal_server_error end
  end

  class BadRequest < ConnectorError
    def status; :bad_request end
  end

  class PaymentError < ConnectorError
    def status; :payment_error end
  end
end
