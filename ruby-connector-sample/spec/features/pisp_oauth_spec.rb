require "rails_helper"

describe OauthController do
  let(:user)    { FactoryBot.create(:user, email: "demobankuser@example.com") }
  let(:token)   { FactoryBot.create(:token, user: user, redirect_url: "https://google.com") }
  let(:adapter) { Sandbox::Adapter }
  let(:account) { FactoryBot.create(:account, user: user) }
  let!(:payment) do
    FactoryBot.create(:payment,
      token:                token,
      user:                 user,
      account:              account,
      confirmation_code:    "456",
      extra: {redirect_url: Settings.priora_demo.base_url}
    )
  end

  it "redirect user with invalid payment_id" do
    visit oauth_confirm_payment_path(payment_id: payment.id + 1)
    expect(page.current_host).to eq(Settings.priora_demo.base_url)
  end

  context "user is not authenticated" do
    it "require credentials" do
      visit oauth_confirm_payment_path(payment_id: payment.id)
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

      expect(page).to have_current_path(oauth_confirm_payment_path(payment_id: payment.id))

      expect(find('.confirm-payment-form')['action']).to eq(oauth_make_payment_path(payment_id: payment.id))

      expect(page).to have_field("credentials[otp]")
      expect(page).to have_link(href: oauth_cancel_payment_path(payment_id: payment.id))
    end
  end

  context "user is authenticated" do
    it "show consent page" do
      pisp_sign_in_user(payment.id)

      expect(find('.confirm-payment-form')['action']).to eq(oauth_make_payment_path(payment_id: payment.id))

      expect(page).to have_field("credentials[otp]")
      expect(page).to have_link(href: oauth_cancel_payment_path(payment_id: payment.id))
    end
  end

  context "pisp actions" do
    before do
      pisp_sign_in_user(payment.id)
    end

    it "Cancel payment" do
      expect(Connector::Payment).to receive(:fail).with(
        payment.priora_payment_id,
        {
          "provider_code" => payment.token.provider_code,
          "error_class"   => "SessionClosed",
          "error_message" => I18n.t("error.message.CanceledByCustomer")
        }
      ).and_return(true)

      click_link("Cancel")

      expect(page.current_host).to     eq(payment.extra["redirect_url"])
      expect(payment.reload.status).to eq("canceled")
    end

    it "Confirm payment" do
      expect(Sandbox::Adapter).to                 receive(:new).with(token).and_call_original
      expect_any_instance_of(Sandbox::Adapter).to receive(:confirm_payment).and_call_original
      expect_any_instance_of(Sandbox::Adapter).to receive(:prepare_confirmation).with(
        "data_params" => {
          "priora_payment_id" => payment.priora_payment_id,
          "confirmation_code" => "456",
          "payment_id"        => payment.id
        }
      ).and_call_original
      expect(Connector::Payment).to receive(:success).with(
        payment.priora_payment_id,
        {"provider_code" => payment.token.provider_code}
      ).and_return(true)

      fill_in "credentials[otp]", with: "456"
      click_on("Confirm")

      expect(page.current_host).to eq(payment.extra["redirect_url"])
      expect(payment.reload.status).to eq("confirmed")
    end

    it "send fail payment if confirmation code is wrong" do
      expect(Sandbox::Adapter).to                 receive(:new).with(token).and_call_original
      expect_any_instance_of(Sandbox::Adapter).to receive(:confirm_payment).and_call_original
      expect_any_instance_of(Sandbox::Adapter).to receive(:prepare_confirmation).with(
        "data_params" => {
          "priora_payment_id" => payment.priora_payment_id,
          "confirmation_code" => "657",
          "payment_id"        => payment.id
        }
      ).and_call_original
      expect(Connector::Payment).to receive(:fail).with(
        payment.priora_payment_id,
        {
          "error_message" => I18n.t("error.message.InvalidConfirmationCode"),
          "error_class"   => Error::External::InvalidConfirmationCode.to_s.demodulize,
          "extra"         => {},
          "provider_code" => token.provider_code
        }
      ).and_return(true)

      fill_in "credentials[otp]", with: "657"
      click_on("Confirm")

      expect(page.current_host).to     eq(payment.extra["redirect_url"])
      expect(payment.reload.status).to eq("failed")
    end
  end
end
