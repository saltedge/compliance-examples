require Rails.root.join("lib/tasks/seeds/base_seeder")

namespace :db do
  desc "Seed fake data"

  task seed: [:environment] do
    BaseSeeder.generate!
  end
end
