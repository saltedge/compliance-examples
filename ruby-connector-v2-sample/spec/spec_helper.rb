RSpec.configure do |config|
  config.before(:each) do
    Redis.current = MockRedis.new
  end
  config.expect_with :rspec do |expectations|
    expectations.include_chain_clauses_in_custom_matcher_descriptions = true
  end
  config.mock_with :rspec do |mocks|
    mocks.verify_partial_doubles = true
  end
end

def adapter_fixture(path)
  JSON.parse File.read(File.join(RSpec.configuration.fixture_path, "test", "adapters", path))
end
