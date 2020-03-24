<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>User consent</title>

    <#include "user.css">
</head>
<body>
    <div class="container">
        <h1 class="top-header">Payment Order Confirmation</h1>
        <div class="info-container">
            <p>From:<#if account_from??>${account_from}</#if>
            <p>To:<#if account_to??>${account_to}</#if>
            <p>Amount:<#if amount??>${amount}</#if>
            <p>Description:<#if description??>${description}</#if>
            <p>
        </div>
        <div class="form-container">
            <#if payment_id??>
                <form action="/oauth/consent/payments" method="post">
                    <input type="hidden" name="payment_id" value="${payment_id}">
                    <input type="hidden" name="user_id" value="${user_id}">
                    <span class="inline">
                        <input type="submit" name="deny" value="Deny" class="submit">
                        <input type="submit" name="confirm" value="Confirm" class="submit">
                    </span>
                </form>
            </#if>
        </div>
    </div>
</body>
</html>