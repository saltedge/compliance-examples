class User < ApplicationRecord
  devise :database_authenticatable, :timeoutable, :validatable

  has_many :tokens, dependent: :destroy
  has_many :accounts, dependent: :destroy

  validates :email, presence: true

  def self.priora_authorize_url(secret:)
    new_user_session_path(session_secret: secret)
  end
end
