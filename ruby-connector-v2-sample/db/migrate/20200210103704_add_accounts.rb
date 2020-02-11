class AddAccounts < ActiveRecord::Migration[5.2]
  def change
    create_table :accounts do |t|
      t.references :user
      t.string :name, null: false
      t.string :currency, null: false
      t.string :iban
      t.jsonb :extra, default: {}
      t.string :cash_account_type
      t.string :product
      t.string :bban
      t.string :bic
      t.string :msisdn
      t.string :status, default: "enabled"
      t.jsonb :balances
      t.timestamps
    end
  end
end
