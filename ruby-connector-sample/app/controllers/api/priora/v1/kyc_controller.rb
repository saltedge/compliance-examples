class Api::Priora::V1::KycController < Api::Priora::V1::BaseController
  def show
    render json: { data: adapter.user.kyc }
  end
end
