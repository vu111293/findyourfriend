<?php
	class Friend{
		public $uid;
		public $fid;
		public $share;
		
		public function Friend($_uid, $_fid, $_share){
			$this->uid=$_uid;
			$this->fid=$_fid;
			$this->share=$_share;
		}
	}
	class FriendManager{
		
		public static function createNewFriend($_uid, $_fid){
			require("opendb.php");
			
			$sql="insert into friend values($_uid, $_fid, DEFAULT)";
			mysql_query($sql);// or die("Query error: ".mysql_error($con));
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function getAllFriendList(){
			$arr=array();
			require("opendb.php");
			
			$sql="select * from friend";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr, new Friend($record['uid'], $record['fid'], $record['share']));
			}
			
			require("closedb.php");
			
			return $arr;
		}
		
		public static function getFriendsById($_id){
			$arr=array();
			require("opendb.php");
			require("history.php");
			require("account.php");
			require("user.php");
			
			$sql="select * from friend where uid=$_id";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr, array("user"=>UserManager::getUserById($record['fid']), "lastlocation"=>HistoryManager::getLastUserHistory($record['fid']), "state"=>UserManager::getAvailable($record['fid']),"numberlist"=>AccountManager::getNumbersById($record['fid']), "share"=>$record["share"]));
			}
			
			//require("closedb.php");
			
			return $arr;
		}
		
		public static function getFriendByFid($_fid){
			$arr=array();
			require("opendb.php");
			require("history.php");
			require("account.php");
			require("user.php");
			
			array_push($arr, array("user"=>UserManager::getUserById($_fid), "lastlocation"=>HistoryManager::getLastUserHistory($_fid), "state"=>UserManager::getAvailable($_fid),"numberlist"=>AccountManager::getNumbersById($_fid), "share"=>"-1"));
			
			
			//require("closedb.php");
			
			return $arr[0];
		}
		
		public static function getFriendIdsById($_id){
			$arr=array();
			require("opendb.php");
			require("history.php");
			require("account.php");
			require("user.php");
			
			$sql="select fid from friend where uid=$_id";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr, $record['fid']);
			}
			
			//require("closedb.php");
			
			return $arr;
		}
		
		public static function setShare($_uid, $_fid, $_share){
			require("opendb.php");
			
			$sql="update friend set share=$_share where uid=$_uid and fid=$_fid";
			mysql_query($sql);// or die("Query error: ".mysql_error($con));
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function stopShare($_uid, $_fid){
			require("opendb.php");
			
			$sql="update friend set share=3 where (uid=$_uid and fid=$_fid) or (uid=$_fid and fid=$_uid)";
			mysql_query($sql);// or die("Query error: ".mysql_error($con));
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function removeFriend($_uid, $_fid){
			require("opendb.php");
			
			$sql="delete from friend where uid=$_uid and fid=$_fid";
			mysql_query($sql);// or die("Query error: ".mysql_error($con));
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function getShare($_uid, $_fid){
			$arr=array();
			require("opendb.php");
			
			$sql="select share from friend where uid=$_uid and fid=$_fid";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr, $record['share']);
				break;
			}
			echo $arr[0];
			
			require("closedb.php");
		}
	}
?>