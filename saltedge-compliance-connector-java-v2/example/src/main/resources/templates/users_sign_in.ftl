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
            <form method="post" action="/users/auth">
                <p>Username: <input type="text" name="username">
                <p>Password: <input type="password" name="password">
                <p><input type="submit" value="Sign In" class="submit">
            </form>

            <#if error??>
                <h3 style="color:red;">${error}</h3>
            </#if>
        </div>
    </div>
</body>
</html>