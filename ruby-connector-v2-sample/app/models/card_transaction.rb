class CardTransaction < ApplicationRecord
  belongs_to :card_account

  VALID_STATUSES = %w(booked pending).freeze
  VALID_STATUSES.each do |name|
    const_set(name.upcase, name)
    define_method("#{name}?") { name == status }
    scope name.to_sym, -> { where(status: name) }
  end

  validates :transaction_details, :status, :currency, :amount, :transaction_date, presence: true
end
