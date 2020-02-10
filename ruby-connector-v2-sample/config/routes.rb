Rails.application.routes.draw do

  default_url_options host: Rails.application.credentials[:default][:url]

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

        resource :errors, only: [:create]
      end
    end
  end
end
