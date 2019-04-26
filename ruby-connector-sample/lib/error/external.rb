module Error
  module External
    class TokenNotFound            < NotFound; end

    class TokenExpired             < Unauthorized; end
    class InvalidCredentials       < Unauthorized; end
    class AccessDenied             < Unauthorized; end

    class InvalidEncoding          < BadRequest; end
    class ValueOutOfRange          < BadRequest; end
    class WrongRequestFormat       < BadRequest; end
    class AuthorizationMissing     < BadRequest; end
    class InvalidAuthorizationType < BadRequest; end
    class JWTDecodeError           < BadRequest; end
    class JWTExpiredSignature      < BadRequest; end
    class JWTVerificationError     < BadRequest; end
    class JWTIncorrectAlgorithm    < BadRequest; end
    class AccessTokenMissing       < BadRequest; end

    class InternalServerError      < ServerError; end

    class InvalidPaymentAttributes < PaymentError; end
    class UnknownConfirmationFlow  < PaymentError; end
    class InvalidAttributeValue    < PaymentError; end
    class InvalidPaymentAccount    < PaymentError; end
    class InsufficientFunds        < PaymentError; end
    class InvalidConfirmationCode  < PaymentError; end
  end
end
