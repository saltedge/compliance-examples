<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Sign In Consent</title>

    <style type="text/css">
        body {
            font-family: Arial, Helvetica, sans-serif;
            background-color:lightgrey;
        }
        .container {
            max-width: 640px;
            margin: 0 auto;
            padding: 10px 10px;
            background-color:white;
        }
        h1 {
            text-align:center;
        }
        fieldset {
            border: 0;
        }
        .submit {
            font-size: larger;
            padding: 5px 10px;
            color: white;
            background-color: blue;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Spring Example Consent</h1>

        <#if session_secret?? && accounts?? && card_accounts??>
            <form action="/oauth/authorize/consent" method="post">
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
        </#if>
    </div>
</body>
</html>