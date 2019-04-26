require "rails_helper"

describe OauthController do
  let(:user)    { FactoryBot.create(:user, email: "demobankuser@example.com") }
  let(:token)   { FactoryBot.create(:token, user: user, redirect_url: "https://priora-demo.saltedge.com") }
  let(:adapter) { Sandbox::Adapter }

  it "redirect user without session_secret provided" do
    visit oauth_grant_access_path
    expect(page.current_host).to eq(Settings.priora_demo.base_url)
  end

  it "redirect user with invalid session_secret" do
    visit oauth_grant_access_path(session_secret: "invalid")
    expect(page.current_host).to eq(Settings.priora_demo.base_url)
  end

  context "user is not authenticated" do
    it "require credentials and otp" do
      visit oauth_grant_access_path(session_secret: token.session_secret)
      expect(page).to have_current_path(oauth_authorize_path)

      ## AUTHORIZATION STEP
      # attempt with invalid credentials
      fill_in "login",    with: "wrong"
      fill_in "password", with: "wrong"
      find("input.btn.btn-default").click
      expect(page).to have_text("Invalid login, password")

      # attempt with right credentials
      fill_in "login",    with: adapter.config.valid_credentials[:login]
      fill_in "password", with: adapter.config.valid_credentials[:password]
      find("input.btn.btn-default").click
      expect(page).to have_current_path(oauth_otp_path)

      ## OTP STEP
      # attempt with invalid otp
      fill_in "otp", with: "wrong"
      find("input.btn.btn-default").click
      expect(page).to have_text("Invalid otp")

      # attempt with right otp
      fill_in "otp", with: adapter.config.interactive_fields_credentials[:otp]
      find("input.btn.btn-default").click

      expect(page).to have_link(href: oauth_grant_path)
      expect(page).to have_link(href: oauth_deny_path)
    end
  end

  context "user is authenticated" do
    it "show the consent page" do
      aisp_sign_in_user(token.session_secret)
      expect(page).to have_link(href: oauth_grant_path)
      expect(page).to have_link(href: oauth_deny_path)
    end
  end

  context "aisp actions" do
    before do
      aisp_sign_in_user(token.session_secret)
      token.update! redirect_url: Settings.priora_demo.base_url
    end

    it "Deny access" do
      expect(Connector::Session).to receive(:fail).with(
        token.session_secret,
        {
          "error_class"   =>"AccessDenied",
          "error_message" =>"Access was denied by the user.",
          "user_id"       => User.last.id,
          "provider_code" => token.provider_code,
          "extra"         => {}
        }.as_json
      ).and_return(true)

      click_link("Deny")
      expect(page.current_host).to   eq(Settings.priora_demo.base_url)
      expect(token.reload.status).to eq(Token::REVOKED)
    end

    it "Grant access" do
      expect(Connector::Session).to receive(:success).with(
        token.session_secret,
        {
          "token"            => token.token,
          "token_expires_at" => token.expires_at,
          "user_id"          => User.last.id,
          "provider_code"    => token.provider_code,
          "extra"            => {}
        }.as_json
      ).and_return(true)

      click_link("Grant")
      expect(page.current_host).to   eq(Settings.priora_demo.base_url)
      expect(token.reload.status).to eq(Token::CONFIRMED)
    end
  end
end
