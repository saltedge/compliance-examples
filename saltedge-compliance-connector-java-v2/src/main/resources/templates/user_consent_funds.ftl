<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Funds Confirmation Consent</title>

    <#include "user.css">
</head>
<body>
    <div class="container">
        <h1 class="top-header">Funds Confirmation Consent</h1>
        <div class="info-container">
            <p>For Account Identifiers:
            <#if account.getIban()??><p>${account.getIban()}</#if>
            <#if account.getBban()??><p>${account.getBban()}</#if>
            <#if account.getMsisdn()??><p>${account.getMsisdn()}</#if>
            <#if account.getPan()??><p>${account.getPan()}</#if>
            <#if account.getMaskedPan()??><p>${account.getMaskedPan()}</#if>
            <p>
        </div>
        <div class="form-container">
            <#if state??>
                <form action="/consent/authorize/funds" method="post">
                    <input type="hidden" name="state" value="${state}">
                    <input type="hidden" name="user_id" value="${user_id}">

                    <br><p>
                    <span class="inline">
                        <button type="submit" name="submit" value="0" class="submit">Deny</button>
                        <button type="submit" name="submit" value="1" class="submit">Confirm</button>
                    </span>
                </form>
            </#if>
        </div>
    </div>
</body>
</html>