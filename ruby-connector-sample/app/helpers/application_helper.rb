module ApplicationHelper
  def render_scopes(scopes)
    scopes.flat_map do |scope|
      next if [Token::SIGN_IN, Token::AUTHENTICATOR].include?(scope)

      if scope == Token::PAYMENTS
        [I18n.t('customer.scopes.verify_payment'), I18n.t('customer.scopes.execute_payment')]
      else
        [I18n.t("customer.scopes.#{scope}"), I18n.t('customer.scopes.offline_access')]
      end
    end.uniq.compact
  end

  def bootstrap_flash(options = {})
    alert_types    = [:success, :info, :warning, :danger]
    flash_messages = []
    flash.each do |type, message|
      # Skip empty messages, e.g. for devise messages set to nothing in a locale file.
      next if message.blank?

      type = type.to_sym
      type = :success if type == :notice
      type = :danger  if type == :alert
      type = :danger  if type == :error
      next unless alert_types.include?(type)

      tag_class = options.extract!(:class)[:class]
      tag_options = {
        class: "alert alert-#{type} #{tag_class}"
      }.merge(options)

      close_button = content_tag(:button, raw("&times;"), type: "button", class: "close", "data-dismiss" => "alert")

      Array(message).each do |msg|
        text = content_tag(:div, close_button + msg, tag_options)
        flash_messages << text if msg
      end
    end
    flash_messages.join("\n").html_safe
  end
end
