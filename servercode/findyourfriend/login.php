<?php
	require("account.php");
	require("appmanager.php");
	if(AppManager::checkAppCode($_REQUEST['appcode'])){
		echo AccountManager::login($_REQUEST['number'], md5($_REQUEST['password']));
	}
?>