<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Accounts Information Consent</title>

    <#include "user.css">
</head>
<body>
    <div class="container">
        <h1 class="top-header">Accounts Information Consent</h1>

        <#if state??>
        <div class="form-container">
            <form action="/consent/authorize/accounts" method="post">
                <#if accounts?? && card_accounts??>
                    <#list accounts>
                        <h2>Accounts</h2>
                        <#items as item>
                            <label><input type="checkbox" name="accounts_ids" value="${item.id}"/>${item.name}</label><br>
                        </#items>
                    <#else>
                        <h2>No Accounts</h2>
                    </#list>
                    <hr>
                    <#list card_accounts>
                        <h2>Cards</h2>
                        <#items as item>
                            <label><input type="checkbox" name="card_accounts_ids" value="${item.id}"/>${item.maskedPan}</label>
                        </#items>
                    <#else>
                        <h2>No Card Accounts</h2>
                    </#list>
                <#elseif preselected_identifiers?? && no_preselected_accounts_error??>
                    <#if no_preselected_accounts_error>
                        <h2>User has no preselected accounts.</h2>
                    <#else>
                        <h2>TPP preselected accounts.</h2>
                    </#if>
                    <p>${preselected_identifiers}</p>
                </#if>
                <hr>
                <input type="hidden" name="state" value="${state}">
                <input type="hidden" name="user_id" value="${user_id}">

                <br><p>
                <span class="inline">
                    <button type="submit" name="submit" value="0" class="submit">Deny</button>
                    <button type="submit" name="submit" value="1" class="submit">Confirm</button>
                </span>
                </p>
            </form>
        </div>
        </#if>
    </div>
</body>
</html>