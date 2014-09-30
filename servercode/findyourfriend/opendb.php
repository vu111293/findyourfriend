<?php
	$host="localhost";
	$user="your user name";
	$pass="your password";
	$db="your db name";
	
	$con=mysql_connect($host, $user, $pass) or die("Not connect");
	mysql_query("SET time_zone='+07:00'");
	mysql_set_charset("utf8", $con);
	mysql_select_db($db) or die("Database is not exist");
?>
