class Api::Priora::V1::PaymentsController < Api::Priora::V1::BaseController
  before_action :validate_token

  def create
    adapter.prepare_payment({"data_params" => data_params, "token_id" => token.id})
    adapter.perform_payment

    render json: { data: "processing" }
  end

  def check_funds
    validate_data_params!("amount", "currency_code", "account")

    adapter.prepare_funds_check({ data_params: data_params, token_id: token.id })
    adapter.check_funds_availability

    render json: { data: "processing" }
  end

  def show
    validate_data_params!("payment_id")

    payment = token.payments.find_by(id: data_params["payment_id"])

    render json: {
      data: {
        id:                 payment.id,
        total:              payment.total.to_f,
        payment_attributes: payment.payment_attributes,
        status:             payment.status,
        fees:               payment.fees,
        extra:              payment.extra,
        priora_payment_id:  payment.priora_payment_id
      }
    }
  end
end
