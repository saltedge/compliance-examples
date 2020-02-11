require 'rails_helper'
require Rails.root.join("lib/tasks/seeds/base_seeder")

RSpec.describe "AIS flow", type: :request do
  let(:priora_base_url) { "https://priora.saltedge.com/api/connectors/v2/" }
  let(:private_key)     { File.read(Rails.root.join('spec/fixtures/', 'private.pem')) }
  let(:priora_update_params) do
    {
      session_secret: session_secret,
      redirect_url:   new_user_session_url(session_secret: session_secret),
      status:         "redirect"
    }
  end

  let!(:session_secret) { "request_id" }
  let!(:data_params) do
    {
      "session_secret"     => session_secret,
      "provider_code"      => "code",
      "redirect_url"       => "www.berlingroup.redirect.url",
      "app_name"           => "TPP name",
      "authorization_type" => "oauth",
      "access"             => {allPsd2: "allAccounts"}
    }
  end
  let(:headers) do
    {
      "HTTP_AUTHORIZATION" => token_from(data_params),
      "HTTP_ACCEPT"        => "application/json",
      "HTTP_CONTENT_TYPE"  => "application/json"
    }
  end
  let(:base_headers) do
    {
      "App-Id"     => Rails.application.credentials.priora[:app_id],
      "App-Secret" => Rails.application.credentials.priora[:app_secret]
    }
  end

  def token_from(payload)
    "Bearer " + JWT.encode(
      {
        exp:  1.minute.from_now.to_i,
        data: payload
      },
      OpenSSL::PKey::RSA.new(private_key),
      'RS256'
    ).to_s
  end

  before do
    BaseSeeder.generate!
    Timecop.freeze
  end
  after { Timecop.return }

  describe "authentication" do
    it "creates unconfirmed token, generates redirect_url and redirects user back alongside notifying priora" do
      # Priora sends request to tokens#create
      expect(RestClient::Request).to receive(:execute).with(
        method:  :patch,
        url:     priora_base_url + "sessions/#{session_secret}/update",
        headers: base_headers.merge("Authorization" => token_from(priora_update_params))
      )

      perform_enqueued_jobs do
        post api_priora_v2_tokens_path, headers: headers
      end

      token = Token.last
      expect(token.session_secret).to eq(session_secret)

      # TPP authorize
      user = User.last
      priora_success_params = {
        session_secret:   token.session_secret,
        token:            token.token,
        token_expires_at: token.expires_at,
        user_id:          user.id
      }

      expect(RestClient::Request).to receive(:execute).with(
        method:  :patch,
        url:     priora_base_url + "sessions/#{session_secret}/success",
        headers: base_headers.merge("Authorization" => token_from(priora_success_params))
      )

      perform_enqueued_jobs do
        expect(
          post user_session_path, params: {user: {email: user.email, password: "123456", session_secret: token.session_secret}}
        ).to redirect_to(token.extra["redirect_url"])
      end

      expect(token.reload.status).to eq(Token::CONFIRMED)
      expect(token.user_id).to       eq(user.id)
    end
  end

  describe "AIS flow" do
    let!(:user)  { User.last }
    let!(:token) do
      Token.create!(
        session_secret: session_secret,
        extra:          {redirect_url: data_params["redirect_url"]},
        status:         Token::CONFIRMED,
        user:           user
      )
    end
    let!(:data_params) do
      {
        "session_secret" => session_secret,
        "provider_code"  => "code"
      }
    end
    let(:headers) do
      {
        "HTTP_ACCEPT"        => "application/json",
        "HTTP_CONTENT_TYPE"  => "application/json",
        "HTTP_ACCESS_TOKEN"  => token.token
      }
    end
    let!(:get_transactions_params) do
      data_params.merge(
        from_date: 3.months.ago.to_date.as_json,
        to_date:   Date.today.as_json
      )
    end

    it "returns all accounts and its transactions related to the user" do
      get api_priora_v2_accounts_path, headers: headers.merge("HTTP_AUTHORIZATION" => token_from(data_params))

      parsed_response = JSON.parse(response.body)["data"].first.except("created_at", "updated_at")
      expect(parsed_response).to eq(user.accounts.last.as_json.except("created_at", "updated_at"))

      get api_priora_v2_account_transactions_path(user.accounts.last.id), headers: headers.merge("HTTP_AUTHORIZATION" => token_from(get_transactions_params))

      parsed_response    = JSON.parse(response.body)["data"]
      first_transaction  = parsed_response.first.except("created_at", "updated_at", "booking_date", "value_date")
      second_transaction = parsed_response.second.except("created_at", "updated_at", "booking_date", "value_date")
      third_transaction  = parsed_response.third.except("created_at", "updated_at", "booking_date", "value_date")

      expect(first_transaction).to  eq(user.accounts.last.transactions.find_by(id: first_transaction["id"]).as_json.except("created_at", "updated_at", "booking_date", "value_date"))
      expect(second_transaction).to eq(user.accounts.last.transactions.find_by(id: second_transaction["id"]).as_json.except("created_at", "updated_at", "booking_date", "value_date"))
      expect(third_transaction).to  eq(user.accounts.last.transactions.find_by(id: third_transaction["id"]).as_json.except("created_at", "updated_at", "booking_date", "value_date"))
    end
  end
end
