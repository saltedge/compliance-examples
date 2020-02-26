<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Authorization Error</title>

    <style type="text/css">
      h1 {text-align:center;}
    </style>
</head>
<body>
    <h1>Spring Example Authorization Error</h1>
    <h1 style="color:red;">Error</h1>

    <#if session_secret??>
        <form action="/oauth/authorize/error" method="post">
           <input type="hidden" name="session_secret" value="${session_secret}">
           <p><input type="submit" value="Close authorization session">
       </form>
   </#if>
</body>
</html>