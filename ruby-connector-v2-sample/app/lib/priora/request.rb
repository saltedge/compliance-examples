class Priora::Request
  def self.base_url
    URI.join(Settings.priora[:host], "api/connectors/v2/").to_s
  end

  def self.update_session(secret:, params:)
    new(mtd: :patch, url: "sessions/#{secret}/update", params: params).execute
  end

  def self.success_session(secret:, params:)
    new(mtd: :patch, url: "sessions/#{secret}/success", params: params).execute
  end

  def self.fail_session(secret:, params:)
    new(mtd: :patch, url: "sessions/#{secret}/fail", params: params).execute
  end

  attr_reader :mtd, :url, :params
  def initialize(mtd:, url:, params: {}, headers: {})
    @mtd = mtd
    @url = url
    @params = params
    @headers = headers
  end

  def execute
    RestClient::Request.execute(method: mtd, url: URI.join(self.class.base_url, url).to_s, headers: headers)
  rescue RestClient::Exception => ex
    puts JSON.parse(ex.response)
  end

  private

  def headers
    @headers.merge(
      "Authorization" => self.class.token_from(params),
      "App-Id" => Settings.priora[:app_id],
      "App-Secret" => Settings.priora[:app_secret]
    )
  end

  def self.token_from(payload)
    "Bearer " + JWT.encode(
      {
        exp:  1.minute.from_now.to_i,
        data: payload
      },
      OpenSSL::PKey::RSA.new(File.read(Settings.priora[:private_key_file])),
      'RS256'
    ).to_s
  end
end
