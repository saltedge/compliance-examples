<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Sign In User</title>

    <style type="text/css">
      h1 {text-align:center;}
    </style>
</head>
<body>
    <h1>Spring Example Sign In</h1>

    <#if session_secret??>
       <form action="/oauth/authorize" method="post">
           <p>Login: <input type="text" name="login">
           <p>Password: <input type="password" name="password">
           <input type="hidden" name="session_secret" value="${session_secret}">
           <p><input type="submit" value="Sign In">
       </form>
    </#if>
</body>
</html>