class Transaction < ApplicationRecord
  STATUSES = ["posted", "pending"]

  belongs_to :account

  STATUSES.each do |status|
    const_set(status.upcase, status)
  end

  validates :description, :currency_code, :amount, :made_on, presence: true
end
