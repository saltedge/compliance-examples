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

ActiveRecord::Schema.define(version: 2019_03_06_160532) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"

  create_table "accounts", force: :cascade do |t|
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
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["user_id"], name: "index_accounts_on_user_id"
  end

  create_table "payments", force: :cascade do |t|
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
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["account_id"], name: "index_payments_on_account_id"
    t.index ["token_id"], name: "index_payments_on_token_id"
    t.index ["user_id"], name: "index_payments_on_user_id"
  end

  create_table "tokens", force: :cascade do |t|
    t.string "token", null: false
    t.datetime "expires_at", null: false
    t.string "session_secret", null: false
    t.string "scopes", null: false
    t.string "provider_code", null: false
    t.string "confirmation_code"
    t.string "status", default: "unconfirmed", null: false
    t.integer "user_id"
    t.text "public_key"
    t.string "refresh_token"
    t.string "user_identifier"
    t.string "authorization_type"
    t.string "app_name"
    t.string "redirect_url"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["user_id"], name: "index_tokens_on_user_id"
  end

  create_table "transactions", force: :cascade do |t|
    t.integer "account_id"
    t.string "currency_code", null: false
    t.decimal "amount", precision: 30, scale: 10, null: false
    t.decimal "balance", precision: 30, scale: 10
    t.string "description", limit: 4000, null: false
    t.date "made_on", null: false
    t.string "status", default: "posted", null: false
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["account_id"], name: "index_transactions_on_account_id"
  end

  create_table "users", force: :cascade do |t|
    t.string "email"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
  end

end
