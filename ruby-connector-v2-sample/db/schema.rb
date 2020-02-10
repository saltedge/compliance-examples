# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 2020_02_10_110544) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"

  create_table "accounts", force: :cascade do |t|
    t.bigint "user_id"
    t.string "name", null: false
    t.string "currency", null: false
    t.string "iban"
    t.jsonb "extra", default: {}
    t.string "cash_account_type"
    t.string "product"
    t.string "bban"
    t.string "bic"
    t.string "msisdn"
    t.string "status", default: "enabled"
    t.jsonb "balances"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["user_id"], name: "index_accounts_on_user_id"
  end

  create_table "tokens", force: :cascade do |t|
    t.string "token", null: false
    t.datetime "expires_at", null: false
    t.string "session_secret", null: false
    t.string "confirmation_code"
    t.string "status", default: "unconfirmed", null: false
    t.jsonb "extra", default: {}
    t.integer "user_id"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["user_id"], name: "index_tokens_on_user_id"
  end

  create_table "transactions", force: :cascade do |t|
    t.bigint "account_id"
    t.float "amount"
    t.string "currency"
    t.string "status", default: "pending"
    t.date "value_date"
    t.date "booking_date"
    t.jsonb "creditor_details"
    t.jsonb "debtor_details"
    t.jsonb "remittance_information", default: {}
    t.jsonb "currency_exchange", default: []
    t.jsonb "extra", default: {}
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["account_id"], name: "index_transactions_on_account_id"
  end

  create_table "users", force: :cascade do |t|
    t.string "login", null: false
    t.string "email", null: false
    t.string "encrypted_password", null: false
    t.jsonb "kyc"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["email"], name: "index_users_on_email", unique: true
    t.index ["login"], name: "index_users_on_login", unique: true
  end

end
