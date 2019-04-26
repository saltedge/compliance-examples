class Connector::Payment < Connector::Base
  class << self
    def update(priora_payment_id, params)
      params["priora_payment_id"] = priora_payment_id
      post("/api/connectors/v1/payments/update", params.as_json)
    end

    def success(priora_payment_id, params)
      params["priora_payment_id"] = priora_payment_id
      post("/api/connectors/v1/payments/success", params.as_json)
    end

    def fail(priora_payment_id, params)
      params["priora_payment_id"] = priora_payment_id
      post("/api/connectors/v1/payments/fail", params.as_json)
    end
  end
end
