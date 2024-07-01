<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Payment Order</title>

    <#include "user.css">
</head>
<body>
    <div class="container">
        <h1 class="top-header">Payment Order</h1>
        <div class="info-container">
            <p>From:<#if account_from??>${account_from}</#if>
            <p>To:<#if account_to??>${account_to}</#if>
            <p>Amount:<#if amount??>${amount}</#if>
            <p>Description:<#if description??>${description}</#if>
            <p>
        </div>
        <div class="form-container">
            <#if state??>
                <form action="/consent/authorize/payments" method="post">
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