class SeedDB
  def self.generate!
    User.destroy_all
    puts "Destroyed all users"

    user = User.create!(
      login: "test",
      email: "test@example.com",
      password: "123456"
    )
    puts "Created user test@example.com:123456"

    account = user.accounts.create!(
      name: "Example name",
      iban: "FK54RAND61068429674579",
      currency: "USD",
      cash_account_type: Account::CASH_ACCOUNT_TYPES.sample,
      product: "Girokonto",
      bban: "4215 4215 6421",
      bic: "BARCGB22XXX",
      msisdn: "447912345678",
      status: Account::ENABLED,
      balances: [{
        type: Account::BALANCE_TYPES.sample,
        currency: "USD",
        amount: "213.35",
        credit_limit_included: false,
        reference_date: Date.yesterday,
        last_changed_date_time: 24.hours.ago,
        last_committed_transaction: "test"
      }]
    )
    puts "Created accounts"

    3.times do |time|
      value_date = time.days.ago
      status = Transaction::STATUSES.sample

      account.transactions.create!(
        value_date: value_date,
        booking_date: status == Transaction::PENDING ? nil : value_date + 1.day,
        currency: account.currency,
        amount: rand(-50..100.0).round(2),
        status: status,
        creditor_details: {
          name: "John Cena",
          account: {
            iban: "US12345",
            currency_code: "USD"
          }
        },
        debtor_details: {
          name: "Barbara Cena",
          account: {
            iban: "US54321",
            currency_code: "EUR"
          }
        },
        remittance_information: {
          structured: "business_services",
          unstructured: "Upwork Pro Plan Subscription"
        },
        currency_exchange: [
          {
            contract_identification: "string",
            exchange_rate: "1.21",
            quotation_date: value_date,
            source_currency: "EUR",
            target_currency: "USD",
            unit_currency: "string"
          }
        ],
        extra: {
          mandate_id: "string",
          check_id: "string",
          purpose_code: "BKDF",
          bank_transaction_code: "PMNT-RCDT-ESCT",
          proprietary_bank_transaction_code: "string"
        }
      )
      puts "Created transactions"
    end
  end
end

namespace :db do
  desc "Seed fake data"

  task seed: [:environment] do
    SeedDB.generate!
  end
end
