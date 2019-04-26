FactoryBot.define do
  factory :user do
    sequence :email do |n| "test#{n}@example.com" end
  end

  factory :token do
    session_secret SecureRandom.uuid
    provider_code  Sandbox::Adapter.code
    scopes         %w(accounts kyc payments)
  end

  factory :payment do
    token
    total { 64 }
    payment_attributes {
      {
        amount: -64.0,
        currency_code: "EUR",
        description: "ADOBE CREATIVE Debit IRELAND ON 28 FEB BDC",
        iban_from: "XF4128335",
        iban_to: "XF5558056"
      }
    }
    fees {
      [
        { description: "Bank fee.",    amount: 0.25, currency_code: "EUR" },
        { description: "Payment fee.", amount: 0.1,  currency_code: "EUR" }
      ]
    }
    status "pending"
    priora_payment_id 42
  end

  factory :account do
    name "MasterCard"
    sort_code "22-33-44"
    nature "credit_card"
    number "554444"
    currency_code "EUR"
    payment_account true
    balance 1123.34
    available_amount 25.11
    extra { {} }
  end
end
