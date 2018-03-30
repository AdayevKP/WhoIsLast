<!DOCTYPE html>
<!--SIGN IN PAGE-->
<html lang="en">
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
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Who is last?</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="#">Очереди</a></li>
                <li><a href="#about">Группы</a></li>
                <li><a href="#contact">Преподаватели</a></li>
            </ul>
        </div>
    </div><!--/.nav-collapse -->
</nav>

<div class="container">

    <form class="form-signin" method="post" action="/test/new_group">
        <h2 class="form-signin-heading" align="middle">Создайте новую группу</h2>
        <label for="inputPassword" class="sr-only">Login</label>
        <input type="text" id="login" name="inputName" class="form-control" placeholder="введите имя группы" required>
        <label for="inputEmail" class="sr-only">Email address</label>
        <input type="email" id="inputEmail" name="inputEmail" class="form-control" placeholder="e-mail адрес" required autofocus>
        <label for="inputPassword" class="sr-only">Password</label>
        <input type="password" id="inputPassword" name="inputPassword" class="form-control" placeholder="пароль" required>


        <button class="btn btn-lg btn-primary btn-block" type="submit">Зарегистрироваться</button>
    </form>

</div> <!-- /container -->
</body>
</html>