class CreateTokens < ActiveRecord::Migration[5.2]
  def change
    create_table :tokens do |t|
      t.string :token, null: false
      t.datetime :expires_at, null: false
      t.string :session_secret, null: false
      t.string :scopes, null: false
      t.string :provider_code, null: false
      t.string :confirmation_code
      t.string :status, default: "unconfirmed", null: false
      t.integer :user_id
      t.text :public_key
      t.string :refresh_token
      t.string :user_identifier
      t.string :authorization_type
      t.string :app_name
      t.string :redirect_url

      t.timestamps
    end

    add_index :tokens, :user_id
  end
end
