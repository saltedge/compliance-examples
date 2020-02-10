class Callbacks::SessionUpdater < ApplicationJob
  def perform(token_id:)
    token = Token.find(token_id)

    raise "Token should be initiated" unless token.initiated?

    Priora::Request.update_session(
      secret: token.session_secret,
      params: {
        session_secret: token.session_secret,
        redirect_url: new_user_session_url(session_secret: token.session_secret),
        status: "redirect" # TODO: constantize
      }
    )
  end
end
