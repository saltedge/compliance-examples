class Api::Priora::V1::TokensController < Api::Priora::V1::BaseController
  before_action :validate_secret

  def create
    adapter.prepare_authorization({"data_params" => data_params})
    adapter.oauth_authorize

    render json: { data: "processing" }
  end

  private

  def validate_secret
    validate_data_params!("session_secret")
  end
end
