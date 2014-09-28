<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" href="<?php echo $base_url; ?>style.css"/>
<link rel="shortcut icon" href="http://sgulab.letsgeekaround.com/img/logo_small.png">
<style>
   table {border-collapse:collapse; table-layout:auto;border:solid 3px #09c;}
   table td {border:solid 1px #09c; white-space:nowrap; padding:2px;}
   table th {border:solid 1px #09c; white-space:nowrap; background:#09C; color:#FFF;}
</style>
<title>Test</title>
</head>
<body style="font-family:Arial, Helvetica, sans-serif; padding:0px; margin:0px;" bgcolor="#EEE" >
	<div style="background:#15cef6; color:#000; padding:2px; font-family:Verdana, Geneva, sans-serif; font-size:2ex;">
    	<img src="img/sgu.png" align=ABSMIDDLE width="35"/>SGU LAB
    </div>
	<div style="">
    	<form method="post" action="index.php">
        	<select name="t" style="vertical-align:central">
              <option value="u">User Data</option>
              <option value="a">Account Data</option>
              <option value="f">Friend Data</option>
              <option value="c">Chat Data</option>
              <option value="h">History Data</option>
              <option value="l">Location Data</option>
            </select>
        	<button style="background:#09C; color:#FFF;">GET</button>
        </form>
    </div>
	<?php
		function show($_h, $_tb){
			$s="Total: ".count($_tb)." record(s)";
			
			$s.='<table>';
			$s.='<tr">';
			foreach($_h as $_key) {
					$s.='<th>'.$_key.'</th>';
			}
			$s.='</tr>';
			foreach($_tb as $_row) {
				$s.='<tr>';
				foreach($_row as $key=>$value) {
					$s.='<td>'.$value.'</td>';
				}
				$s.='</tr>';
			}
			$s.='</table><br />';
			echo $s;
		}
		function show2($_h, $_tb){
			$s="Total: ".count($_tb)." record(s)";
			
			$s.='<table>';
			$s.='<tr">';
			foreach($_h as $_key) {
					$s.='<th>'.$_key.'</th>';
			}
			$s.='</tr>';
			foreach($_tb as $_row) {
				$s.='<tr>';
					$s.='<td>'.$_row.'</td>';
				$s.='</tr>';
			}
			$s.='</table><br />';
			echo $s;
		}
	?>
    <?php
		switch($_POST["t"]){
			case "u":
				echo "<h1 style='color:#09C'>USER DATA</h1>";
				require_once("user.php");
				$_hx=array("id", "name", "avatarlink", "gcmid", "lastlogin", "gender", "address", "birthday", "school", "workplace", "email", "fblink", "ispublic");
				$_tbx=UserManager::getAllUsers();
				show($_hx, $_tbx);
				break;
			case "a":
				echo "<h1 style='color:#09C'>ACCOUNT DATA</h1>";
				require_once("account.php");
				$_hx=array("number");
				$_tbx=AccountManager::getAllAcounts();
				show2($_hx, $_tbx);
				break;
			case "f":
				echo "<h1 style='color:#09C'>FRIEND DATA</h1>";
				require_once("friend.php");
				$_hx=array("uid", "fid", "share");
				$_tbx=FriendManager::getAllFriendList();
				show($_hx, $_tbx);
				break;
			case "c":
				echo "<h1 style='color:#09C'>CHAT DATA</h1>";
				require_once("chats.php");
				$_hx=array("timest", "sender", "recipient", "msg");
				$_tbx=ChatsManager::getAllChats();
				show($_hx, $_tbx);
				break;
			case "h":
				echo "<h1 style='color:#09C'>HISTORY DATA</h1>";
				require_once("history.php");
				$_hx=array("timest", "lat", "lng");
				$_tbx=HistoryManager::getAllUserHistory();
				show($_hx, $_tbx);
				break;
			case "l":
				echo "<h1 style='color:#09C'>LOCATION DATA</h1>";
				require_once("location.php");
				$_hx=array("lat","lng","street", "sublocality", "locality", "adminstrative_area", "country", "fulladdr");
				$_tbx=LocationManager::getAllLocationData();
				show($_hx, $_tbx);
				break;
			default:
				break;
		}
	?>
</body>
</html>