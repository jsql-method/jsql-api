<?php
	session_start();
	ini_set('date.timezone', 'Europe/Warsaw');
	date_default_timezone_set('Europe/Warsaw');

	header("Content-Type: application/json; charset=UTF-8");


	if( rand(0,2) == 1 )
	{
		usleep( rand(500,1500) );
	} else 
	{
		sleep( rand(1,3) );
	}
	

	$obj = new \stdClass();

	$step = $_SESSION['buildApi_step'];

	if($step == 0 || $step == 100)
	{
		$obj->text = "[". date('H:i:s') . "] Build started..";
		$_SESSION['buildApi_step'] = 1;
	} 
	elseif (0 < $step && $step < 10) 
	{
		$obj->text = "[". date('H:i:s') . "] Step " . $_SESSION['buildApi_step'] . "/10..";
		$_SESSION['buildApi_step']++;
	} 
	elseif ($step == 10) 
	{
		$obj->text = "[". date('H:i:s') . "] Build completed!";
		$_SESSION['buildApi_step'] = 100;
	}

	$obj->code = $_SESSION['buildApi_step'];

	echo json_encode($obj);
?>