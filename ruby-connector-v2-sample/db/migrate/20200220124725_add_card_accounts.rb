class AddCardAccounts < ActiveRecord::Migration[5.2]
  def change
    create_table :card_accounts do |t|
      t.references :user
      t.string :name,       null: false
      t.string :masked_pan, null: false
      t.string :currency,   null: false
      t.string :product,    null: false
      t.string :status,     null: false

      t.jsonb :credit_limit
      t.jsonb :balances
      t.jsonb :extra

      t.timestamp
    end
  end
end
