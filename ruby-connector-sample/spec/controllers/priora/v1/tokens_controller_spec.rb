require "rails_helper"

describe Api::Priora::V1::TokensController, type: :controller do
  let(:user)               { FactoryBot.create :user }
  let(:adapter)            { Sandbox::Adapter }
  let(:priora_private_key) { File.read(Rails.root.join('spec/fixtures', 'test_priora_private.pem')) }
  let(:params) do
    {
      "data" => {
        "session_secret" => "1",
        "client_id" => 123,
        "app_name" => "Test Application",
        "original_request" => {
          "client_payload" => {
            "data" => {
              "provider_code" => adapter.code,
              "credentials" => {
                "authorization_type" => "oauth"
              },
              "scopes" => ["accounts", "transactions", "kyc"],
              "public_key" => "public_key"
            }
          }
        }
      },
      "exp" => Time.now.to_i + 120
    }
  end
  let(:token) { JWT.encode(params, OpenSSL::PKey::RSA.new(priora_private_key), 'RS256') }

  before do
    @request.headers['AUTHORIZATION'] = "Bearer #{token}"
  end

  describe "POST create" do
    it "launches PrioraAuthorize actions" do
      expect(Connector::Session).to receive(:update).with(
        params["data"]["session_secret"],
        {
          "status"        => "redirect",
          "extra"         => {"redirect_url" => adapter.oauth_url(params["data"]["session_secret"])},
          "provider_code" => adapter.code
        }
      ).and_return(true)

      expect { post :create, format: :json }.to change(Token, :count).by(1)

      data = JSON.parse(response.body)
      expect(data).to eq({ "data" => "processing" })
    end
  end
end
