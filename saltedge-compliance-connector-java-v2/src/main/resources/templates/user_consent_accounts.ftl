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

        <#if session_secret?? && accounts?? && card_accounts??>
        <div class="form-container">
            <form action="/consent/authorize/accounts" method="post">
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