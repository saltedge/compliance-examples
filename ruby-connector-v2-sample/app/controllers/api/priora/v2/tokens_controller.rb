class Api::Priora::V2::TokensController < Api::Priora::V2::BaseController
  def create
    token = Token.create!(
      session_secret: params["session_secret"],
      extra: {redirect_url: params["redirect_url"]}
    )
    Callbacks::SessionUpdater.perform_later(token_id: token.id)
    render priora_ok
  end
end
