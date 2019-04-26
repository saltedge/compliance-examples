class Api::Priora::V1::TransactionsController < Api::Priora::V1::BaseController
  def index
    render json: { data: fetch_transactions }
  end

private

  def fetch_transactions()
    account    = adapter.user.accounts.find_by(id: params[:account_id])
    collection = account.transactions.select(%Q{
      transactions.id,
      transactions.account_id,
      transactions.currency_code,
      transactions.amount,
      transactions.description,
      transactions.made_on,
      transactions.status,
      transactions.balance
    })

    collection = if params[:from_date] && params[:to_date]
      collection.where(%Q{
        transactions.made_on >= '#{params[:from_date]}' AND transactions.made_on <= '#{params[:to_date]}'
      })
    else
      collection
    end

    collection.as_json.map do |transaction|
      transaction.merge("extra" => {"balance" => transaction["balance"]})
    end
  end
end
