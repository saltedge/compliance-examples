class Api::Priora::V1::BaseController < Api::BaseController
private

  def validate_token
    raise Error::External::AccessTokenMissing unless request.headers["HTTP_ACCESS_TOKEN"]
  end

  def adapter
    @adapter ||= Sandbox::Adapter.new(token)
  end
end
