class Callbacks::SessionSuccesser < ApplicationJob
  def perform(token_id:)
    token = Token.find(token_id)

    raise "Token should be confirmed" unless token.confirmed?

    Priora::Request.success_session(
      secret: token.session_secret,
      params: {
        session_secret: token.session_secret,
        token: token.token,
        token_expires_at: token.expires_at,
        user_id: token.user_id
      }
    )
  end
end
