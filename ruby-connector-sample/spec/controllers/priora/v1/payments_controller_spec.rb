require "rails_helper"

describe Api::Priora::V1::PaymentsController, type: :controller do
  let(:user)               { FactoryBot.create :user }
  let(:token)              { FactoryBot.create :token, user: user }
  let(:account)            { FactoryBot.create(:account, user: user) }
  let(:adapter)            { Sandbox::Adapter }
  let(:priora_private_key) { File.read(Rails.root.join('spec/fixtures', 'test_priora_private.pem')) }
  let(:params) do
    {
      "data" => {
        "priora_payment_id" => 119,
        "payment_type" => "internal_transfer",
        "original_request" => {
          "client_payload" => {
            "data" => {
              "payment_attributes" => {
                "from_account" => account.number,
                "to_account" => "DE12345678123456781232",
                "amount" => "12.11",
                "currency_code" => account.currency_code,
                "description" => "water",
                "information" => ""
              },
              "template_id" => "3",
              "provider_code" => adapter.code,
              "redirect_url" => "https://payment_confirmed.com",
              "extra" => {}
            }
          }
        }
      },
      "exp" => Time.now.to_i + 120
    }
  end

  describe "POST create" do
    let(:payload) { JWT.encode(params, OpenSSL::PKey::RSA.new(priora_private_key), 'RS256') }

    it "launches process of creating a payment" do
      @request.headers["AUTHORIZATION"]     = "Bearer #{payload}"
      @request.headers["HTTP_ACCESS_TOKEN"] = token.token

      expect(Connector::Payment).to             receive(:update).with(any_args).and_return(true)
      expect { post :create, format: :json }.to change(Payment, :count).by(1)

      data = JSON.parse(response.body)
      expect(data).to eq({ "data" => "processing" })

      payment = user.payments.last
      expect(payment.status).to            eq(Transaction::PENDING)
      expect(payment.confirmation_code).to eq(adapter.config.payment_confirmation_credentials[:otp])
      expect(payment.total).to             eq(params["data"]["original_request"]["client_payload"]["data"]["payment_attributes"]["amount"].to_f)
    end
  end

  describe "GET show" do
    let(:payment) { FactoryBot.create :payment, user: user, account: account, token: token }
    let(:params)  { {"data" => {"payment_id" => payment.id}} }
    let(:payload) { JWT.encode(params, OpenSSL::PKey::RSA.new(priora_private_key), 'RS256') }

    it "returns payment data" do
      @request.headers['AUTHORIZATION']     = "Bearer #{payload}"
      @request.headers["HTTP_ACCESS_TOKEN"] = token.token

      get :show, format: :json

      data = JSON.parse(response.body)["data"]
      expect(data).to eq({
        "id"                 => payment.id,
        "total"              => payment.total.to_f,
        "payment_attributes" => payment.payment_attributes,
        "status"             => payment.status,
        "fees"               => payment.fees,
        "extra"              => payment.extra,
        "priora_payment_id"  => payment.priora_payment_id
      })
    end
  end
end
