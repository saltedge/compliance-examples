class Transaction < ApplicationRecord
  belongs_to :account

  STATUSES = %w(booked pending).each { |status| const_set status.upcase, status }
end
