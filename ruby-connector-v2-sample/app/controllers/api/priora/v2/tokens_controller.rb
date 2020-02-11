class Api::Priora::V2::TokensController < Api::Priora::V2::BaseController
  def create
    token = Token.create!(
      session_secret: jwt_params["session_secret"],
      extra: {redirect_url: jwt_params["redirect_url"]}
    )
    Callbacks::SessionUpdater.perform_later(token_id: token.id)
    render priora_ok
  end
end
