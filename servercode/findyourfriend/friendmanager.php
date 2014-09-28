<?php
	require('friend.php');
	require("appmanager.php");
	if(AppManager::checkAppCode($_REQUEST['appcode'])){
		switch($_REQUEST['type']){
			case 'GET_FRIEND_LIST':
				echo json_encode(array("friends"=>FriendManager::getFriendsById($_REQUEST['id'])));
				break;
			case 'GET_FRIEND_BY_FID':
				echo json_encode(FriendManager::getFriendByFid($_REQUEST['fid']));
				break;
			case 'FRIEND_REMOVE':
				echo (FriendManager::removeFriend($_REQUEST['uid'], $_REQUEST['fid']) + FriendManager::removeFriend($_REQUEST['fid'], $_REQUEST['uid']));
				break;
			case 'SET_SHARE':
				echo FriendManager::setShare($_REQUEST['uid'], $_REQUEST['fid'], $_REQUEST['share']);
				break;
			case 'STOP_SHARE':
				echo FriendManager::stopShare($_REQUEST['uid'], $_REQUEST['fid']);
				break;
			case 'GET_SHARE':
				echo FriendManager::getShare($_REQUEST['uid'], $_REQUEST['fid']);
				break;
			default:
				echo "Unknown type";
		}
	}
?>