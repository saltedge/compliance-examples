class CardAccount < ApplicationRecord
  belongs_to :user
  has_many :card_transactions, dependent: :destroy

  VALID_BALANCE_TYPES = %w(
    closingBooked expected openingBooked interimAvailable forwardAvailable
    interimBooked openingAvailable previouslyClosedBooked closingAvailable information
  )
  VALID_STATUSES = %w(enabled deleted blocked)

  VALID_STATUSES.each do |name|
    const_set(name.upcase, name)
  end

  validates :name, :masked_pan, :currency, :product, :status, presence: true
end
