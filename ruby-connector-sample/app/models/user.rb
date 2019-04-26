class User < ApplicationRecord
  has_many :tokens,       dependent: :destroy
  has_many :accounts,     dependent: :destroy
  has_many :transactions, through: :accounts
  has_many :payments,     dependent: :destroy

  def kyc
    JSON.parse(File.read(Rails.root.join("db", "fixtures", "user.json")))["kyc"]
  end
end
