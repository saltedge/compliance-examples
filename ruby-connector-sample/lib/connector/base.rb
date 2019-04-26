class Connector::Base
  class << self
    def get(path, params, headers = {})
      request(path, params, "GET", headers)
    end

    def post(path, params, headers = {})
      request(path, params, "POST", headers)
    end

    def request(path, params, verb, headers = {})
      payload = {
        exp: 1.minute.from_now.to_i,
        data: params.except("provider_code")
      }

      token = JWT.encode(payload, OpenSSL::PKey::RSA.new(Settings.connector_private_key), 'RS256')
      headers.merge!(
        "Accept"        => "application/json",
        "Content-type"  => "application/json",
        "App-id"        => Settings.priora_app.dig("app_id"),
        "App-secret"    => Settings.priora_app.dig("app_secret"),
        "Authorization" => "Bearer #{token}"
      )

      RestClient::Request.execute(
        method:  verb,
        url:     Settings.priora.base_url + path,
        headers: headers
      )
    rescue RestClient::Exception => error
      pp JSON.parse(error.response)
    end
  end
end
