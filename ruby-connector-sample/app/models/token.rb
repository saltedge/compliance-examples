class Token < ApplicationRecord
  STATUSES = %w(unconfirmed confirmed revoked)
  SCOPES   = %w(authenticator accounts kyc payments sign_in transactions)
  scope :active, -> { where("expires_at > now() AND status = 'confirmed'") }

  before_create :assign_token

  belongs_to :user, optional: true
  has_many :payments
  has_many :sessions

  serialize :scopes, JSON

  (STATUSES + SCOPES).each do |status|
    const_set(status.upcase, status)
  end

  STATUSES.each do |name|
    define_method("#{name}?") { status == name }
  end

  def active?
    expires_at > Time.now.utc
  end

  def assign_token(with_refresh=true)
    self.token         = SecureRandom.urlsafe_base64(32)
    self.expires_at    = 30.days.from_now.utc.change(usec: 0)
    self.refresh_token = SecureRandom.urlsafe_base64(32) if with_refresh
  end
end
