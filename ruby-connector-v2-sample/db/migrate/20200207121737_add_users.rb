class AddUsers < ActiveRecord::Migration[5.2]
  def change
   create_table :users do |t|
      t.string :login,              null: false
      t.string :email,              null: false
      t.string :encrypted_password, null: false
      t.jsonb  :kyc

      t.timestamps null: false
    end

    add_index :users, :email, unique: true
    add_index :users, :login, unique: true
  end
end

