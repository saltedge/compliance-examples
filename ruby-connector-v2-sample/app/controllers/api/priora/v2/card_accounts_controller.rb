class Api::Priora::V2::CardAccountsController < Api::Priora::V2::BaseController
  def index
    render json: {data: current_token.user.card_accounts.as_json}
  end

  def transactions
    render json: {data: current_token.user.card_accounts.find_by(id: params[:account_id])&.transactions || []}
  end

private

  def account
    @account ||= token.user.card_accounts.find_by(id: params[:account_id])
    return @account if @account
    raise Error::External::AccountNotFound.new(attribute: :id, value: params[:account_id])
  end
end
