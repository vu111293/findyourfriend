<?php
	require("appmanager.php");
	require("chats.php");
	require("friend.php");
	$_default_msg="HELP ME!";
	if(AppManager::checkAppCode($_REQUEST['appcode'])){
		switch($_REQUEST['type']){
			case 'SEND_MSG':
				echo ChatsManager::send_msg(new Chats(NULL, $_REQUEST['senderid'], $_REQUEST['recipientid'], $_REQUEST['msg']));
				break;
			case 'GET_CHATS':
				echo json_encode(ChatsManager::getAllChatsWithSbById($_REQUEST['senderid'], $_REQUEST['recipientid']));
				break;
			case 'REMOVE_CHATS':
				echo json_encode(ChatsManager::removeChats($_REQUEST['senderid'], $_REQUEST['recipientid']));
				break;
			case 'REMOVE_ALL_CHATS':
				echo json_encode(ChatsManager::removeAllChats($_REQUEST['senderid']));
				break;
			case 'GET_CHATS_BY_ID':
				echo json_encode(ChatsManager::getAllChatsById($_REQUEST['id']));
				break;
			case 'SEND_FRIEND_REQUEST':
				echo ChatsManager::send_msg(new Chats(NULL, $_REQUEST['senderid'], $_REQUEST['recipientid'], $_REQUEST['msg']));
				FriendManager::createNewFriend($_REQUEST['senderid'], $_REQUEST['recipientid']);
				FriendManager::setShare($_REQUEST['senderid'], $_REQUEST['recipientid'], 0);
				FriendManager::createNewFriend($_REQUEST['recipientid'], $_REQUEST['senderid']);
				FriendManager::setShare($_REQUEST['recipientid'], $_REQUEST['senderid'], 1);
				break;
			case 'SEND_FRIEND_NOT_ACCEPT':
				echo ChatsManager::send_msg(new Chats(NULL, $_REQUEST['senderid'], $_REQUEST['recipientid'], $_REQUEST['msg']));
				FriendManager::removeFriend($_REQUEST['recipientid'], $_REQUEST['senderid']);
				FriendManager::removeFriend($_REQUEST['senderid'], $_REQUEST['recipientid']);
				break;
			case 'SEND_FRIEND_ACCEPT':
				echo ChatsManager::send_msg(new Chats(NULL, $_REQUEST['senderid'], $_REQUEST['recipientid'], $_REQUEST['msg']));
				FriendManager::setShare($_REQUEST['recipientid'], $_REQUEST['senderid'], 2);
				FriendManager::setShare($_REQUEST['senderid'], $_REQUEST['recipientid'], 2);
				break;
			case 'SEND_SHARE_REQUEST':
				echo ChatsManager::send_msg(new Chats(NULL, $_REQUEST['senderid'], $_REQUEST['recipientid'], $_REQUEST['msg']));
				FriendManager::setShare($_REQUEST['senderid'], $_REQUEST['recipientid'], 3);
				FriendManager::setShare($_REQUEST['recipientid'], $_REQUEST['senderid'], 4);
				break;
			case 'SEND_SHARE_NOT_ACCEPT':
				echo ChatsManager::send_msg(new Chats(NULL, $_REQUEST['senderid'], $_REQUEST['recipientid'], $_REQUEST['msg']));
				FriendManager::setShare($_REQUEST['recipientid'], $_REQUEST['senderid'], 2);
				FriendManager::setShare($_REQUEST['senderid'], $_REQUEST['recipientid'], 2);
				break;
			case 'SEND_SHARE_ACCEPT':
				echo ChatsManager::send_msg(new Chats(NULL, $_REQUEST['senderid'], $_REQUEST['recipientid'], $_REQUEST['msg']));
				FriendManager::setShare($_REQUEST['recipientid'], $_REQUEST['senderid'], 5);
				FriendManager::setShare($_REQUEST['senderid'], $_REQUEST['recipientid'], 5);
				break;
			case 'HELP_MSG':
				$_myfriends=FriendManager::getFriendIdsById($_REQUEST['senderid']);
				foreach($_myfriends as $_fid){
					ChatsManager::send_msg(new Chats(NULL, $_REQUEST['senderid'], $_fid, $_default_msg));	
				}
				break;
			default:
				echo "Unknown type";
		}
	}
?>
