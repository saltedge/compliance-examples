class Account < ApplicationRecord
  belongs_to :user
  has_many :transactions, dependent: :destroy

  constantize = Proc.new { |const| const_set const.upcase, const }
  STATUSES = %w(enabled blocked deleted).each(&constantize)
  CASH_ACCOUNT_TYPES = %w(
    CACC CASH CISH COMM CPAC LLSV LOAN MGLD MOMA NREX
    ODFT ONDP OTHR SACC SLRY SVGS TAXE TRAN TRAS
  ).each(&constantize)
  BALANCE_TYPES = %w(
    closingBooked expected openingBooked interimAvailable forwardAvailable
    interimBooked openingAvailable previouslyClosedBooked closingAvailable information
  ).each(&constantize)

end
