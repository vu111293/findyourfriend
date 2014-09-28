<?php
	require("user.php");
	require("appmanager.php");
	require("location.php");
	require("activation/sendverify.php");
	require("forgotpassword/sendverify.php");
	if(AppManager::checkAppCode($_REQUEST['appcode'])){
		switch($_REQUEST['type']){
			case 'CREATE':
				$regex = '/^[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$/';
			
				if(preg_match($regex, $_REQUEST['email']))
				{ 
					$_user=new User(NULL, $_REQUEST['name'], $_REQUEST['avatar'], $_REQUEST['gcmid'], NULL, $_REQUEST['gender'], $_REQUEST['address'], $_REQUEST['birthday'], $_REQUEST['school'], $_REQUEST['workplace'], $_REQUEST['email'], $_REQUEST['fblink'], $_REQUEST['ispublic']);
					$_code=md5($_REQUEST['email'].time());
					$_number=$_REQUEST['number'];
					$_id=UserManager::createNewUser_Temp($_user, $_number, $_code);
					if($_id!=NULL && $_id>0){
						$_password=md5($_REQUEST['password']);
						UserManager::createNewUserLogin($_id, $_password);
						sendVerify($_id, $_REQUEST['email'], $_code);
					}
					echo $_id;
				}
				else echo -1;
				break;
			case 'RESEND':
				$_numberresend=$_REQUEST['number'];
				$arr=UserManager::getUsersTempByNumber($_numberresend);
				$_code=md5($_REQUEST['email'].time());
				foreach($arr as $u){
					sendVerify($u->id, $u->email, $_code);
					UserManager::updateUserActiveCode($u->id, $_code);
				}
				break;
			case 'FORGET_PASSWORD':
				$_number=$_REQUEST['number'];
				$_email=UserManager::getEmailByNumber($_number);
				$_code=md5($_email.time());
				$_id=UserManager::requireChangePassword($_number, $_email, $_code);
				if($_id!=NULL && $_id>0) {
					sendVerifyPwd($_id, $_email, $_code);
				}
				echo $_id;
				break;
			case 'EDIT':
				$_user=new User(NULL, $_REQUEST['name'], $_REQUEST['avatar'], $_REQUEST['gcmid'], NULL, $_REQUEST['gender'], $_REQUEST['address'], $_REQUEST['birthday'], $_REQUEST['school'], $_REQUEST['workplace'], $_REQUEST['email'], $_REQUEST['fblink'], $_REQUEST['ispublic']);
				echo UserManager::editUser($_REQUEST['id'], $_user);
				break;
			case 'DELETE':
				echo UserManager::removeUser($_REQUEST['id']);
				break;
			case 'GET_USER_LIST':
				echo json_encode(array('users'=>UserManager::getAllUsers()));
				break;
			case 'GET_USER_LIST_BY_NAME':
				echo json_encode(array('users'=>UserManager::getAllUsersByName($_REQUEST['id'], $_REQUEST['name'])));
				break;
			case 'GET_USER_LIST_BY_ADDRESS':
				echo json_encode(array('users'=>UserManager::getAllUsersByAddress($_REQUEST['id'], $_REQUEST['address'])));
				break;
			case 'GET_USER_LIST_BY_SCHOOL':
				echo json_encode(array('users'=>UserManager::getAllUsersBySchool($_REQUEST['id'], $_REQUEST['school'])));
				break;
			case 'GET_USER_LIST_BY_WORKPLACE':
				echo json_encode(array('users'=>UserManager::getAllUsersByWorkplace($_REQUEST['id'], $_REQUEST['workplace'])));
				break;
			case 'GET_USER_LIST_BY_GENDER':
				echo json_encode(array('users'=>UserManager::getAllUsersByGender($_REQUEST['id'], $_REQUEST['gender'])));
				break;
			case 'GET_USER_LIST_BY_YEARS':
				echo json_encode(array('users'=>UserManager::getAllUsersByYears($_REQUEST['id'], $_REQUEST['from'], $_REQUEST['to'])));
				break;
			case 'GET_USER_LIST_BY_STHG':
				echo json_encode(array('users'=>UserManager::getAllUsersBySthg($_REQUEST['id'], $_REQUEST['sthg'])));
				break;
			case 'GET_USER_LIST_BY_ADVANCE':
				echo json_encode(array('users'=>UserManager::getAllUsersByAdvance($_REQUEST['id'], $_REQUEST['name'], $_REQUEST['gender'], $_REQUEST['address'], $_REQUEST['from'], $_REQUEST['to'], $_REQUEST['school'], $_REQUEST['workplace'])));
				break;
			case 'GET_USER_BY_ID':
				echo json_encode(UserManager::getUserById($_REQUEST['id']));
				break;
			case 'GET_PROFILE':
				echo json_encode(UserManager::getMyProfile($_REQUEST['id']));
				break;
			case 'GET_USER_STATE':
				echo UserManager::getAvailable($_REQUEST['id']);
				break;
			case 'CHANGE_PASSWORD':
				echo UserManager::changePassword($_REQUEST['id'], md5($_REQUEST['oldpass']), md5($_REQUEST['newpass']));
				break;
			case 'CHANGE_AVATAR':
				echo UserManager::changeUserAvatar($_REQUEST['id'], $_REQUEST['avatar']);
				break;
			case 'UPDATE_GCMID':
				echo UserManager::updateGcmId($_REQUEST['id'], $_REQUEST['gcmid']);
				break;
			case 'SET_PUBLIC':
				echo UserManager::setPublic($_REQUEST['id'], $_REQUEST['ispublic']);
				break;
			case 'GET_NEW_USERLIST':
				echo json_encode(UserManager::getNewUserIdListByNumberList($_REQUEST['id'], $_REQUEST['numberlist']));
				break;
			case 'FIND_IN_DISTANCE':
				echo json_encode(LocationManager::getUserInDistance($_REQUEST['lat'], $_REQUEST['lng'], $_REQUEST['d']));
				break;
			case 'FIND_IN_CURRENT_ADDRESS':
				echo json_encode(LocationManager::getUserInCurAddress($_REQUEST['addr']));
				break;
			/*case 'FIND_IN_ADDRESS':
				echo json_encode(array('users'=>UserManager::getUserByAddress($_REQUEST['addr'])));
				break;*/
			default:
				echo "Unknown type";
				break;
		}
	}
	else{
		echo "You don't have permission!";
	}
?>