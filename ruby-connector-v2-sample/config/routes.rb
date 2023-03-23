Rails.application.routes.draw do

  protocol = Rails.env.staging? || Rails.env.production? ? :https : :http
  default_url_options host: Settings.application.host, protocol: protocol

  devise_for :users, controllers: {
    sessions: "users/sessions"
  }

  root to: "dashboards#index"

  namespace :api do
    namespace :priora do
      namespace :v2 do
        resources :tokens, only: [:create] do
          patch :revoke, on: :collection
        end

        resources :accounts, only: [:index] do
          get :transactions, to: "accounts#transactions"
        end

        resources :card_accounts, only: [:index] do
          collection do
            scope ":account_id" do
              get "transactions", to: "card_accounts#transactions"
            end
          end
        end

        resource :errors, only: [:create]
      end
    end
  end
end
