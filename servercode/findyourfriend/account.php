<?php
	/**
	 * Account class for login
	 */
	
	class AccountManager{
		public static function getAllAcounts(){
			$arr=array();
			require("opendb.php");
			
			$sql="select * from account";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr, $record['number']);
			}
			
			require("closedb.php");
			
			return $arr;
		}
		//do after create new user
		public static function createNewAccount($_ursId, $_number){
			require("opendb.php");
			
			$sql="insert into account values($_ursId, '$_number')";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function deleteAccount($_id, $_number){
			require("opendb.php");
			
			$sql="delete from account where id=$_id and number='$_number'";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function deleteAllAccountOfId($_id){
			require("opendb.php");
			
			$sql="delete from account where id=$_id";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function checkExistAccount($_id, $_number){
			require("opendb.php");
			
			$arr=array();
			$sql="select count(*) as counter from account where id=$_id and number='$_number'";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			
			while($record=mysql_fetch_array($rs)){
				array_push($arr, $record['counter']);
				break;
			}
			return $arr[0];
		}
		
		public static function checkExistNumber($_number){
			require("opendb.php");
			
			$arr=array();
			$sql="select count(*) as counter from account where number='$_number'";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			
			while($record=mysql_fetch_array($rs)){
				array_push($arr, $record['counter']);
				break;
			}
			return $arr[0];
		}
		
		// for sercurity ???
		public static function getCurrentUserId($_number){
			$arr=array();
			require("opendb.php");
			
			$sql="select id from account where number='$_number'";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr, $record['id']);
			}
			
			require("closedb.php");
			
			return $arr[0];
		}
		
		public static function getNumbersById($_id){
			$arr=array();
			require("opendb.php");
			
			$sql="select number from account where id='$_id'";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr, $record['number']);
			}
			
			require("closedb.php");
			
			return $arr;
		}
		
		public static function getUserByNumber($_number){
			$arr=array();
			require("opendb.php");
			require("user.php");
			
			$sql="select * from account as A, usermain as B, userdetail as C where A.number='$_number' and A.id=B.id and B.id=C.id";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				if($record['ispublic']==1){
					$u=new User(
								$record['id'],
								$record['name'],
								$record['avatar'],
								$record['gcmid'],
								$record['lastlogin'],
								$record['gender'],
								$record['address'],
								$record['birthday'],
								$record['school'],
								$record['workplace'],
								$record['email'],
								$record['fblink'],
								$record['ispublic']
								);
				}
				else{
					$u=new User(
								$record['id'],
								$record['name'],
								$record['avatar'],
								$record['gcmid'],
								$record['lastlogin'],
								NULL,
								NULL,
								NULL,
								NULL,
								NULL,
								NULL,
								NULL,
								$record['ispublic']
								);
				}
				array_push($arr, $u);
				break;
			}
			
			require("closedb.php");
			
			return $arr[0];
		}
		public static function login($_number, $_password){
			$arr=array();
			$_id=0;
			require("opendb.php");
			require("user.php");
			
			$sql="select A.id from account as A, userlogin as B where A.number='$_number' and B.password='$_password' and A.id=B.id and B.isactive=1";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				$_id=$record['id'];
				UserManager::updateLastestLogin($_id);
				break;
			}
			if($_id==0){
				require("opendb.php");
				$_id2=0;
				$sql2="select id from user_temp where number='$_number'";
				$rs2=mysql_query($sql2) or die("Query error: ".mysql_error($con));
				while($record=mysql_fetch_array($rs2)){
					$_id2=$record['id'];
					break;
				}
				require("closedb.php");
				if($_id2!=0) return -1;
				
			}
			return $_id;
		}
	}
?>
