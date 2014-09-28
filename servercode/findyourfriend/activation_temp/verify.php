<?php
	require_once("../user.php");
	$u=UserManager::getUserTemp($_REQUEST['email'], $_REQUEST['id'], $_REQUEST['hash']);
	UserManager::createNewUser($u);
?>