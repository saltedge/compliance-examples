<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dashboard</title>
    <#include "user.css">
</head>
<body>
    <h1 class="top-header">User Dashboard (ID: ${user_id})</h1>
    <div class="container">
        <h2>Consents</h2>

        <#list tokens>
            <table border="1" class="center">
                <tr>
                   <th>Access Token</th>
                   <th>[X]</th>
                </tr>
            <#items as item>
                <tr>
                    <td>${item}</td>
                    <td>
                        <form method="post" action="/users/dashboard/revoke">
                            <input type="hidden" name="user_id" value="${user_id}">
                            <input type="hidden" name="access_token" value="${item}">
                            <input class='btn btn-primary' type='submit' value='X'>
                        </form>
                    </td>
                </tr>
            </#items>
            </table>
        <#else>
            <p>No active tokens</p>
        </#list>
    </div>
    <br>
</body>
</html>