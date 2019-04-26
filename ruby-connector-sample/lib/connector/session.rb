class Connector::Session < Connector::Base
  class << self
    def update(session_secret, params)
      params["session_secret"] = session_secret
      post("/api/connectors/v1/sessions/update", params)
    end

    def success(session_secret, params)
      params["session_secret"] = session_secret
      post("/api/connectors/v1/sessions/success", params)
    end

    def fail(session_secret, params)
      params["session_secret"] = session_secret
      post("/api/connectors/v1/sessions/fail", params)
    end
  end
end
