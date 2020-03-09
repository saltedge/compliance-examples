<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Sign-In To Spring Example</title>

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
        .submit {
            font-size: larger;
            padding: 5px 10px;
            color: white;
            background-color: blue;
        }

        .input-wrapper, .wrapper {
            width: 100%;
            display: inline-flex;
        }
        .form-wrapper {
            width: 250px;
            display: flex;
            flex-direction: column;
            align-items: center;
        }
        .form-input {
            margin-left: 10px;
            position: relative;
            clear: both;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Sign-In To Spring Example</h1>

        <#if session_secret??>
            <div class="form-wrapper">
                <h3>Input credentials</h3>
                <form method="post" action="/oauth/authorize">
                    <div class="input-wrapper">
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" class="form-input">
                    </div>

                    <div class="input-wrapper">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" class="form-input">
                    </div>

                    <input type="hidden" name="session_secret" value="${session_secret}">

                    <div class="with-margin centered">
                        <input type="submit" value="Login" style="font-size:20px">
                    </div>
                </form>

                <#if error??>
                    <h3 style="color:red;">${error}</h3>
                <#else>
                    <p></p>
                </#if>
            </div>
        </#if>
    </div>
</body>
</html>