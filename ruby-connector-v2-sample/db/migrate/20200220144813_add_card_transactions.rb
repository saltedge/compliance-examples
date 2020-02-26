class AddCardTransactions < ActiveRecord::Migration[5.2]
  def change
    create_table :card_transactions do |t|
      t.references :card_account
      t.date       :transaction_date,    null: false
      t.string     :status,              null: false
      t.string     :currency,            null: false
      t.string     :amount,              null: false
      t.string     :transaction_details, null: false
      t.string     :terminal_id

      t.date       :booking_date

      t.jsonb      :currency_exchange
      t.jsonb      :original_amount

      t.jsonb      :markup_fee
      t.string     :markup_fee_percentage

      t.string     :card_acceptor_id
      t.jsonb      :card_acceptor_address

      t.string     :merchant_category_code
      t.string     :masked_pan
      t.boolean    :invoiced
      t.string     :proprietary_bank_transaction_code

      t.timestamp
    end
  end
end
