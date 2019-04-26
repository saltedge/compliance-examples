class CreateAccounts < ActiveRecord::Migration[5.2]
  def change
    create_table :accounts do |t|
      t.integer "user_id"
      t.string "name"
      t.string "number"
      t.string "currency_code"
      t.string "sort_code"
      t.string "nature"
      t.text "extra"
      t.boolean "payment_account", default: false
      t.decimal "balance", precision: 30, scale: 10, null: false
      t.decimal "available_amount", precision: 30, scale: 10, null: false
      t.decimal "credit_limit", precision: 30, scale: 10

      t.timestamps
    end

    add_index :accounts, :user_id
  end
end
