class CreatePayments < ActiveRecord::Migration[5.2]
  def change
    create_table :payments do |t|
      t.integer "token_id", null: false
      t.integer "user_id"
      t.integer "account_id"
      t.string "priora_payment_id", null: false
      t.decimal "total", null: false
      t.string "status", null: false
      t.string "confirmation_code"
      t.text "fees"
      t.text "extra"
      t.text "payment_attributes"
      t.string "user_identifier"
      t.text "internal_data"
      t.string "description", default: "Transfer"

      t.timestamps
    end

    add_index :payments, :token_id
    add_index :payments, :user_id
    add_index :payments, :account_id
  end
end
