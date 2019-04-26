module Helpers
  def aisp_sign_in_user(session_secret)
    visit oauth_grant_access_path(session_secret: session_secret)
    fill_in "login",    with: Sandbox::Adapter.config.valid_credentials[:login]
    fill_in "password", with: Sandbox::Adapter.config.valid_credentials[:password]
    find("input.btn.btn-default").click
    fill_in "otp", with: Sandbox::Adapter.config.interactive_fields_credentials[:otp]
    find("input.btn.btn-default").click
  end

  def pisp_sign_in_user(payment_id)
    visit oauth_confirm_payment_path(payment_id: payment_id)
    fill_in "login",    with: Sandbox::Adapter.config.valid_credentials[:login]
    fill_in "password", with: Sandbox::Adapter.config.valid_credentials[:password]
    find("input.btn.btn-default").click
  end
end
