<!DOCTYPE html>
<!--CHECK IN PAGE-->
<html lang="en" xmlns:th="http://www.thymeleaf.org">
  <head>
	<head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title></title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <link rel="stylesheet" href="../../resources/css/bootstrap.min.css">
        <style>
            body {
                padding-top: 50px;
                padding-bottom: 20px;
            }
        </style>
        <link rel="stylesheet" href="../../resources/css/bootstrap-theme.min.css">
        <link rel="stylesheet" href="../../resources/css/main.css">
		<link rel="stylesheet" href="../../resources/styles/signin.css">

        <script src="../../resources/js/vendor/modernizr-2.8.3-respond-1.4.2.min.js"></script>
    </head>
  </head>

  <body>

    <div class="container">

      <form class="form-signin" method="post" action="/test/signup">
        <h2 class="form-signin-heading" align="middle">Зарегистрируйтесь</h2>
          <label for="inputPassword" class="sr-only">Login</label>
          <input type="text" id="login" name="inputName" class="form-control" placeholder="введите логин" required>
        <label for="inputEmail" class="sr-only">Email address</label>
        <input type="email" id="inputEmail" name="inputEmail" class="form-control" placeholder="e-mail адрес" required autofocus>
        <label for="inputPassword" class="sr-only">Password</label>
        <input type="password" id="inputPassword" name="inputPassword" class="form-control" placeholder="пароль" required>


        <button class="btn btn-lg btn-primary btn-block" type="submit">Зарегистрироваться</button>
      </form>

    </div> <!-- /container -->
  </body>
</html>
