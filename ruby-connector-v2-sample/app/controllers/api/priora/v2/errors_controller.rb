class Api::Priora::V2::ErrorsController < Api::Priora::V2::BaseController
  def create
    puts "Failed response for #{params[:request][:method]} #{params[:request][:url]}"
    puts "#{params[:error][:error_class]} #{params[:error][:error_message]}"
    puts JSON.pretty_generate(params)
    render priora_ok
  end
end
