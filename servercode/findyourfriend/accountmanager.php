<?php
	require('account.php');
	require("appmanager.php");
	if(AppManager::checkAppCode($_REQUEST['appcode'])){
		switch($_REQUEST['type'])
		{
			case 'CREATE':
				echo AccountManager::createNewAccount($_REQUEST['id'], $_REQUEST['number']);
				break;
			case 'DELETE_ONE':
				echo AccountManager::deleteAccount($_REQUEST['id'], $_REQUEST['number']);
				break;
			case 'DELETE_ALL':
				echo AccountManager::deleteAllAccountOfId($_REQUEST['id']);
				break;
			case 'CHECK_EXIST_ACCOUNT':
				echo AccountManager::checkExistAccount($_REQUEST['id'], $_REQUEST['number']);
				break;
			case 'CHECK_EXIST_NUMBER':
				echo AccountManager::checkExistNumber($_REQUEST['number']);
				break;
			case 'GET_USER_BY_NUMBER':
				echo json_encode(AccountManager::getUserByNumber($_REQUEST['number']));
				break;
			case 'GET_NUMBER_BY_ID':
				echo json_encode(AccountManager::getNumbersById($_REQUEST['id']));
				break;
			default:
				echo "Unknown type";
				break;
		}
	}
?>
