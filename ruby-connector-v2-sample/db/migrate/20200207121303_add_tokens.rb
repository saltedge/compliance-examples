class AddTokens < ActiveRecord::Migration[5.2]
  def change
    create_table :tokens do |t|
      t.string :token, null: false
      t.datetime :expires_at, null: false
      t.string :session_secret, null: false
      t.string :confirmation_code
      t.string :status, default: :unconfirmed, null: false
      t.jsonb :extra, default: {}
      t.integer :user_id
      t.datetime :created_at, null: false
      t.datetime :updated_at, null: false
      t.index ["user_id"], name: :index_tokens_on_user_id
    end
  end
end
