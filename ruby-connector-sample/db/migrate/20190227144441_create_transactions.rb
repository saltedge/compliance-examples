class CreateTransactions < ActiveRecord::Migration[5.2]
  def change
    create_table :transactions do |t|
      t.integer "account_id"
      t.string "currency_code", null: false
      t.decimal "amount", precision: 30, scale: 10, null: false
      t.decimal "balance", precision: 30, scale: 10
      t.string "description", limit: 4000, null: false
      t.date "made_on", null: false
      t.string "status", default: "posted", null: false
      t.datetime "created_at", null: false
      t.datetime "updated_at", null: false

      t.timestamps
    end

    add_index :transactions, :account_id
  end
end
