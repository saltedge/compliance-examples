Rails.application.routes.draw do
  root "application#homepage"

  namespace :oauth do
    get  :grant_access
    get  :authorize
    get  :otp
    post :grant
    post :deny
    post :verify_authorization
    post :verify_otp
  end

  get 'oauth/confirm_payment/:payment_id', to: 'oauth#confirm_payment', as: :oauth_confirm_payment
  post 'oauth/cancel_payment/:payment_id', to: 'oauth#cancel_payment',  as: :oauth_cancel_payment
  post 'oauth/make_payment/:payment_id',   to: 'oauth#make_payment',    as: :oauth_make_payment

  namespace :api, defaults: { format: :json } do
    namespace :priora do
      namespace :v1 do
        namespace :tokens do
          post :create
        end
        namespace :payments do
          get :show
          post :create, :check_funds
        end
        get :kyc,          to: 'kyc#show'
        get :transactions, to: 'transactions#index'
        get :accounts,     to: 'accounts#index'
      end
    end
  end
end
