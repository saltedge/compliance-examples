class Api::Priora::V2::AccountsController < Api::Priora::V2::BaseController
  def index
    render json: {data: {accounts: current_token.user.accounts.as_json}}
  end

  def transactions
    # NOTE: validate and serialize
    render json: {
      data: {
        transactions: current_token.user.accounts.find_by(id: params[:account_id])&.transactions || []
      }
    }
  end
end
