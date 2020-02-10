class Token < ApplicationRecord
  belongs_to :user, optional: true

  before_create :set_defaults

  STATUSES = %w(initiated confirmed).each do |status|
    const_set(status.upcase, status)
    define_method("#{status}?") { self.status == status }
  end

  def set_defaults
    self.token ||= SecureRandom.uuid
    self.expires_at ||= 90.days.from_now
    self.status = INITIATED
  end
end
