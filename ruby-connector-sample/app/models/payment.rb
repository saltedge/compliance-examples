class Payment < ApplicationRecord
  belongs_to :user, optional: true
  belongs_to :account, optional: true
  belongs_to :token

  %i(fees extra payment_attributes internal_data).each do |field|
    serialize field, JSON
  end
end
