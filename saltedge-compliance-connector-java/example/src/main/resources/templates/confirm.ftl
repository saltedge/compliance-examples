<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Payment confirmation</title>

    <style type="text/css">
      h1 {text-align:center;}
    </style>
</head>
<body>
    <h1>Spring Example Payment Confirmation</h1>

    <#if submit_to?? && payment_id??>
       <form action="${submit_to}" method="post">
           <p>Confirm code: <input type="text" name="confirmation_code">
           <input type="hidden" name="payment_id" value="${payment_id}">
           <p><input type="submit" value="Submit code">
       </form>
    </#if>

</body>
</html>