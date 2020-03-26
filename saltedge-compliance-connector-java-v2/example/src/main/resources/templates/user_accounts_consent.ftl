<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>User consent</title>

    <#include "user.css">
</head>
<body>
    <div class="container">
        <h1 class="top-header">User Consent for Accounts Information</h1>

        <#if session_secret?? && accounts?? && card_accounts??>
        <div class="form-container">
            <form action="/oauth/consent/accounts" method="post">
                <#list accounts>
                    <h2>Accounts</h2>
                    <#items as item>
                        <h3 class="account-name">${item.name}</h3>
                        <fieldset>
                            <label><input type="checkbox" name="balances" value="${item.id}"/> Balance</label>
                            <label><input type="checkbox" name="transactions" value="${item.id}"/> Transactions</label>
                        </fieldset>
                    </#items>
                <#else>
                    <h2>No Accounts</h2>
                </#list>
                <hr>
                <#list card_accounts>
                    <h2>Cards</h2>
                    <#items as item>
                        <h3 class="account-name">${item.maskedPan}</h3>
                        <fieldset>
                            <label><input type="checkbox" name="card_balances" value="${item.id}"/> Balance</label>
                            <label><input type="checkbox" name="card_transactions" value="${item.id}"/> Transactions</label>
                        </fieldset>
                    </#items>
                <#else>
                    <h2>No Card Accounts</h2>
                </#list>

                <hr>

                <input type="hidden" name="session_secret" value="${session_secret}">
                <input type="hidden" name="user_id" value="${user_id}">
                <p><input type="submit" value="Allow" class="submit"></p>
            </form>
        </div>
        </#if>
    </div>
</body>
</html>