USER_DATA     = JSON.parse(File.read(Rails.root.join("db", "fixtures", "user.json"))).without("kyc")
ACCOUNTS_DATA = JSON.parse(File.read(Rails.root.join("db", "fixtures", "accounts.json")))
TR_COUNT      = 34
DESCRIPTIONS  = [
  "Online Account Transfer",
  "Online Overseas Payment",
  "Online Overseas Payment Charge"
]

user = User.find_or_initialize_by(email: USER_DATA["email"])
user.update!(USER_DATA)
user.save!

user.accounts.destroy_all
accounts = user.accounts.create!(ACCOUNTS_DATA)

def generate_transactions_for(account, n=TR_COUNT)
  n.times do
    transaction = account.transactions.new(
      made_on:       rand(0..30).days.ago,
      amount:        rand(-2000.0..1200.0).round(2),
      currency_code: account.currency_code,
      description:   DESCRIPTIONS.shuffle.first
    )

    yield transaction if block_given?

    transaction.save!
  end
end

accounts.each_with_index do |account, index|
  balance = account.balance
  generate_transactions_for(account, TR_COUNT / (index + 1)) do |transaction|
    balance += transaction.amount
    transaction.balance = balance
  end

  account.update!(balance: balance, available_amount: balance)
end
