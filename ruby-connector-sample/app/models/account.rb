class Account < ApplicationRecord
  NATURES = [
    "account",
    "card",
    "debit_card",
    "credit_card",
    "checking",
    "savings",
    "investment",
    "bonus",
    "loan",
    "credit",
    "insurance",
    "ewallet",
    "mortgage"
  ]

  before_validation :set_number

  belongs_to :user
  has_many :transactions, dependent: :destroy

  serialize :extra, JSON

  NATURES.each do |nature|
    const_set(nature.upcase, nature)
  end

  validates :nature, inclusion: { in: NATURES }
  validates :number, presence: true

  def set_number
    return if self.number.present?
    self.number = (SecureRandom.random_number * 100_000_000).floor
  end
end
