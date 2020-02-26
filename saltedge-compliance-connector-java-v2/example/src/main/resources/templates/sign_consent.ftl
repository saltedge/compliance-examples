<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Sign In Consent</title>

    <style type="text/css">
      h1 {text-align:center;}
    </style>
</head>
<body>
    <h1>Spring Example Consent</h1>

    <#if session_secret?? && accounts??>
        <form action="/oauth/authorize/consent" method="post">

                <fieldset>
                    <legend>Select accounts scopes</legend>
                    <#list accounts as account>
                    <p>
                        <label> Account: ${account.name}</label>
                        <label><input type="checkbox" name="balances" value="${account.id}"/> Balance</label>
                        <label><input type="checkbox" name="transactions" value="${account.id}"/> Transactions</label>
                    </p>
                    </#list>
                </fieldset>

            <input type="hidden" name="session_secret" value="${session_secret}">
            <input type="hidden" name="user_id" value="${user_id}">
            <p>
                <input type="submit" value="Allow">
            </p>
        </form>

       <form action="/oauth/authorize/error" method="post">
            <input type="hidden" name="session_secret" value="${session_secret}">
            <p><input type="submit" value="Deny">
        </form>
    </#if>
</body>
</html>