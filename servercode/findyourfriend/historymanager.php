<?php
	require('history.php');
	require("appmanager.php");
	require("location.php");
	if(AppManager::checkAppCode($_REQUEST['appcode'])){
		switch($_REQUEST['type']){
			case 'GET_HISTORY':
				echo json_encode(array("history"=>HistoryManager::getUserHistory($_REQUEST['id'])));
				break;
			case 'GET_LAST_LOCATION':
				echo json_encode(HistoryManager::getLastUserHistory($_REQUEST['id']));
				break;
			case 'CREATE_HISTORY':
				$rs = HistoryManager::addNewHistory($_REQUEST['id'], new History(NULL, $_REQUEST['latitude'], $_REQUEST['longtitude']));
				if($rs>0){
					$rs=LocationManager::updateNewLocation($_REQUEST['id'], $_REQUEST['latitude'], $_REQUEST['longtitude']);
				}
				echo $rs;
				break;
			case 'REMOVE_HISTORY':
				echo HistoryManager::removeHistory($_REQUEST['id']);
				break;
			default:
				echo "Unknown type";
				break;
		}
	}
?>