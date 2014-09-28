<?php
	//---000webhost
	/*$host="mysql6.000webhost.com";
	$user="a1196806_sgudev";
	$pass="sgu123456";
	$db="a1196806_sgudev";*/
	
	//---orgfree.com
	/*$host="localhost";
	$user="839807";
	$pass="flamingo1";
	$db="839807";*/
	
	//---uni.cc
	/*$host="sql205.oni.cc";
	$user="onic_15172818";
	$pass="flamingo1";
	$db="onic_15172818_sgulab";*/
	
	$host="localhost";
	$user="sgulabna_01";
	$pass="v1t2h3vutoanhung";
	$db="sgulabna_fyf";
	
	$con=mysql_connect($host, $user, $pass) or die("Not connect");
	mysql_query("SET time_zone='+07:00'");
	mysql_set_charset("utf8", $con);
	mysql_select_db($db) or die("Database is not exist");
?>