<?php

	$login = $_POST['login'];
	$password = $_POST['password'];

	$_SESSION['login'] = $login;
	$_SESSION['password'] = $password;

	echo ($login != null && $password != null);
?>