class Api::Priora::V1::AccountsController < Api::Priora::V1::BaseController
  def index
    render json: { data: fetch_accounts }
  end

private

  def fetch_accounts
    adapter.user.accounts.select(%Q{
      accounts.id,
      accounts.name,
      accounts.number,
      accounts.sort_code,
      accounts.currency_code,
      accounts.nature,
      accounts.balance,
      accounts.available_amount,
      accounts.credit_limit,
      accounts.payment_account,
      accounts.extra
    }).order("ID ASC").as_json
  end
end
