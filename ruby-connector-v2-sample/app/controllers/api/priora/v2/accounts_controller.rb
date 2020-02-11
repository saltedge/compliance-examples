class Api::Priora::V2::AccountsController < Api::Priora::V2::BaseController
  def index
    render json: {data: current_token.user.accounts.as_json}
  end

  def transactions
    # NOTE: validate and serialize
    render json: {data: current_token.user.accounts.find_by(id: jwt_params[:account_id])&.transactions || []}
  end
end
