<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>User Sign In</title>

    <#include "user.css">
</head>
<body>
    <div class="container">
        <h1 class="top-header">User Sign In</h1>

        <h3 class="centered">${input_title}</h3>
        <div class="form-container">
            <form method="post" action="/user/consent">
                <p>Username [username]: <input type="text" name="username">
                <p>Password [secret]:   <input type="password" name="password">

                <#if scope??><input type="hidden" name="scope" value="${scope}"></#if>
                <#if session_secret??><input type="hidden" name="session_secret" value="${session_secret}"></#if>
                <#if payment_id??><input type="hidden" name="payment_id" value="${payment_id}"></#if>

                <br><p>
                <span class="inline">
                    <button type="submit" name="submit" value="0" class="submit">Go back</button>
                    <button type="submit" name="submit" value="1" class="submit">Sign In</button>
                </span>
            </form>

            <#if error??>
                <h3 style="color:red;">${error}</h3>
            </#if>
        </div>

    </div>
</body>
</html>