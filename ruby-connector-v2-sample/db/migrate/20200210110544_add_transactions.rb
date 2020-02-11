class AddTransactions < ActiveRecord::Migration[5.2]
  def change
    create_table :transactions do |t|
      t.references :account
      t.float :amount
      t.string :currency
      t.string :status, default: "pending"
      t.date :value_date
      t.date :booking_date
      t.jsonb :creditor_details
      t.jsonb :debtor_details
      t.jsonb :remittance_information, default: {}
      t.jsonb :currency_exchange, default: []
      t.jsonb :extra, default: {}
      t.timestamps
    end
  end
end
