<!DOCTYPE html>
<html lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />

  <title>Confirm | Consent</title>

  <!-- CSS  -->
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css" media="screen,projection">

  <link href="css/style.css" type="text/css" rel="stylesheet" media="screen,projection"/>

</head>
<body>
  <nav class="light-blue lighten-1" role="navigation">
    <div class="nav-wrapper">
      <a id="logo-container" href="/" class="brand-logo center">Spring Bank</a>
    </div>
  </nav>

  <main>
    <center>
      <div class="section"></div>

      <div class="container">
        <div class="z-depth-1 grey lighten-4 row" style="display: inline-block; padding: 24px 24px 24px 24px; border: 1px solid #EEE;">

          <h5 class="black-text">${title}</h5>
          <#if error??>
            <p><span class="red-text">${error}</span></p>
          <#else>
            <p><span class="black-text">${description}</span></p>
            <#if identifier??>
              <p><span class="black-text">Identifier: ${identifier}</span></p>
            </#if>
          </#if>
          <p><span class="black-text">Auth code: ${auth_code}</span></p>

          <#if error??>
            <p><span class="red-text">Nothing to confirm</span></p>
          <#else>
            <form class="col s12" method="post">
              <center>
                <div class='row'>
                  <button type='submit' name='action' value='deny' class='col s4 btn btn-large waves-effect red'>Deny</button>
                  <span class='col s4'></span>
                  <button type='submit' name='action' value='confirm' class='col s4 btn btn-large waves-effect indigo'>Confirm</button>

                  <input type="hidden" name="user_id" value="${user_id}">
                  <input type="hidden" name="consent_auth_code" value="${auth_code}">
                  <#if identifier??>
                    <input type="hidden" name="identifier" value="${identifier}">
                  </#if>
                </div>
              </center>
            </form>
          </#if>
        </div>
      </div>
    </center>

    <div class="section"></div>
    <div class="section"></div>
  </main>

  <!--  Scripts-->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
</body>
</html>