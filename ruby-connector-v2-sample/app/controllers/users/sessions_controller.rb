class Users::SessionsController < Devise::SessionsController

  def new
    # TODO: store in session and clean session_cookie if session_secret doesn't match
    @session_secret = params[:session_secret]
    super
  end

  def create
    super do |user|
      token = Token.find_by(session_secret: params[:user][:session_secret])
      if token
        token.update!(status: Token::CONFIRMED, user_id: current_user.id)
        Callbacks::SessionSuccesser.perform_later(token_id: token.id)
        return redirect_to token.extra["redirect_url"]
      end
    end
  end
end
