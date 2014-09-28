<?php
	class Chats{
		
		//attributes
		public $timest;
		public $sender;
		public $recipient;
		public $msg;
		
		public function Chats($_timest, $_sender, $recipient, $msg){
			$this->timest=$_timest;
			$this->sender=$_sender;
			$this->recipient=$recipient;
			$this->msg=$msg;
		}
	}
	
	class ChatsManager{
		
		public static function getAllChatsById($_id){
			$arr=array();
			require("opendb.php");
			
			$sql="select * from chats where sender=$_id or recipient=$_id order by timest";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				$u=new Chats(
							$record['timest'],
							$record['sender'],
							$record['recipient'],
							$record['msg']
							);
				array_push($arr, $u);
			}
			
			require("closedb.php");
			
			return $arr;
		}
		
		public static function getAllChats(){
			$arr=array();
			require("opendb.php");
			
			$sql="select * from chats";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				$u=new Chats(
							$record['timest'],
							$record['sender'],
							$record['recipient'],
							$record['msg']
							);
				array_push($arr, $u);
			}
			
			require("closedb.php");
			
			return $arr;
		}
		
		public static function getAllChatsWithSbById($_idsender, $_idrecipient){
			$arr=array();
			require("opendb.php");
			
			$sql="select * from chats where (sender=$_idsender and recipient=$_idrecipient) or (sender=$_idrecipient and recipient=$_idsender) order by timest";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				$u=new Chats(
							$record['timest'],
							$record['sender'],
							$record['recipient'],
							$record['msg']
							);
				array_push($arr, $u);
			}
			
			require("closedb.php");
			
			return $arr;
		}
		
		public static function addNewChats($_chats){
			require("opendb.php");
			
			$sql="insert into chats values(NOW(), $_chats->sender, $_chats->recipient, '$_chats->msg')";
			mysql_query($sql);// or die("Query error: ".mysql_error($con));
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function removeAllChats($_id){
			require("opendb.php");
			
			$sql="delete from chats where sender=$_id or recipient=$_id";
			mysql_query($sql) or die("Query error: ".mysql_error($con));
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function removeChats($_uid, $_fid){
			require("opendb.php");
			
			$sql="delete from chats where (sender=$_uid and recipient=$_fid) or (sender=$_fid and recipient=$_uid)";
			mysql_query($sql) or die("Query error: ".mysql_error($con));
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function send_msg($_chats){
			//require("opendb.php");
			require_once("function.php");
			require_once("user.php");
			
			ChatsManager::addNewChats($_chats);
			$_gcmid=array(UserManager::getGcmIdById($_chats->recipient));
			return send_push_notification($_gcmid, $message = array("price" => $_chats->msg));
			//else echo 0;
			
			//require("closedb.php");
		}
		
	}
?>