<?php
	/**
	 * User Information Object Class
	 */
	class User {
		
		//attributes
		public $id;
		public $name;
		public $avatar;
		public $gcmid;
		public $lastlogin;
		public $gender;
		public $address;
		public $birthday;
		public $school;
		public $workplace;
		public $email;
		public $fblink;
		public $ispublic;
		
		public function User($_id, $_name, $_avatar,$_gcmid,$_lastlogin, $_gender, $_address, $_birthday, $_school, $_workplace, $_email,  $_fblink, $_ispublic) {
			$this->id=$_id;
			$this->name=$_name;
			$this->avatar=$_avatar;
			$this->gcmid=$_gcmid;
			$this->lastlogin=$_lastlogin;
			$this->gender=$_gender;
			$this->address=$_address;
			$this->birthday=$_birthday;
			$this->school=$_school;
			$this->workplace=$_workplace;
			$this->email=$_email;
			$this->fblink=$_fblink;
			$this->ispublic=$_ispublic;
		}
	}
	
	/**
	 * 
	 */
	class UserManager{
		
		public static function createNewUser_Temp($_user, $_number, $_activecode){
			require("opendb.php");
			
			$sql="insert into user_temp values(null, '$_user->name', '$_user->avatar', '$_user->gcmid', NOW(), $_user->gender, '$_user->address', '$_user->birthday', '$_user->school', '$_user->workplace', '$_user->email', '$_user->fblink', $_user->ispublic, '$_number', '$_activecode')";
			mysql_query($sql) or die("Query error: ".mysql_error($con));
			$_id=0;
			if(mysql_affected_rows()>0) $_id=mysql_insert_id();
			
			require("closedb.php");
			return $_id;
		}
		
		public static function requireChangePassword($_number, $_email, $_activecode){
			require("opendb.php");
			
			$sql="select id from account where number='$_number'";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			
			$_idx=0;
			while($record=mysql_fetch_array($rs)){
				$_idx=$record['id'];
				break;
			}
			if($_idx!=0){
			
				$sql="insert into forget_temp values($_idx, '$_email', '$_activecode')";
				mysql_query($sql) or die("Query error: ".mysql_error($con));
			}
			require("closedb.php");
			return $_idx;
		}
		
		public static function createNewUserLogin($_id, $_password){
			require("opendb.php");
			
			$sql="insert into userlogin values($_id, '$_password', DEFAULT)";
			mysql_query($sql) or die("Query error: ".mysql_error($con));
			$rs=mysql_affected_rows();
			
			require("closedb.php");
			return $rs;
		}
		
		public static function createNewUser($_user){
			require("opendb.php");
			
			$sql1="insert into usermain values($_user->id, '$_user->name', '$_user->avatar', '$_user->gcmid', '$_user->lastlogin')";
			$sql2="insert into userdetail values($_user->id, $_user->gender, '$_user->address', '$_user->birthday', '$_user->school', '$_user->workplace', '$_user->email', '$_user->fblink', $_user->ispublic)";
			mysql_query($sql1) or die("Query error: ".mysql_error($con));
			$rs=mysql_affected_rows();
			mysql_query($sql2) or die("Query error: ".mysql_error($con));
			$rs=$rs + mysql_affected_rows();
			
			require("closedb.php");
			return $rs;
		}
		
		public static function activeUserLogin($_id){
			require("opendb.php");
			
			$sql="update userlogin set isactive=1 where id=$_id";
			mysql_query($sql) or die("Query error: ".mysql_error($con));
			
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function editUser($_id, $_user){
			require("opendb.php");
			
			$sql1="update usermain set name='$_user->name', avatar='$_user->avatar', gcmid='$_user->gcmid', lastlogin=NOW() where id=$_id";
			$sql2="update userdetail set gender=$_user->gender, address='$_user->address', birthday='$_user->birthday', school='$_user->school', workplace='$_user->workplace', email='$_user->email', fblink='$_user->fblink', ispublic=$_user->ispublic where id=$_id";
			mysql_query($sql1) or die("Query error: ".mysql_error($con));
			$rs=mysql_affected_rows();
			mysql_query($sql2) or die("Query error: ".mysql_error($con));
			$rs=$rs + mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function updateGcmId($_id, $_gcmid){
			require("opendb.php");
			
			$sql="update usermain set gcmid='$_gcmid' where id=$_id";
			mysql_query($sql) or die("Query error: ".mysql_error($con));
			
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function removeUser($_id){
			require("opendb.php");
			
			$sql1="delete from usermain where id=$_id";
			$sql2="delete from userdetail where id=$_id";
			mysql_query($sql1) or die("Query error: ".mysql_error($con));
			$rs=mysql_affected_rows();
			mysql_query($sql2) or die("Query error: ".mysql_error($con));
			$rs=$rs + mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function removeUserTemp($_id, $_email, $_code){
			require("opendb.php");
			
			$sql="delete from user_temp where email='$_email' and activecode='$_code' and id=$_id";
			mysql_query($sql) or die("Query error: ".mysql_error($con));
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function removeAllFogetTemp($_id, $_email){
			require("opendb.php");
			
			$sql="delete from forget_temp where id=$_id and email='$_email'";
			mysql_query($sql) or die("Query error: ".mysql_error($con));
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function updateLastestLogin($_id){
			require("opendb.php");
			
			$sql="update usermain set lastlogin=NOW() where id=$_id";
			mysql_query($sql) or die("Query error: ".mysql_error($con));
			
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function changeUserAvatar($_id, $_avatar){
			require("opendb.php");
			
			$sql="update usermain set avatar='$_avatar' where id=$_id";
			mysql_query($sql) or die("Query error: ".mysql_error($con));
			
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function setPublic($_id, $_ispublic){
			require("opendb.php");
			
			$sql="update userdetail set ispublic=$_ispublic where id=$_id";
			mysql_query($sql) or die("Query error: ".mysql_error($con));
			
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function getAllUsers() {
			$arr=array();
			require("opendb.php");
			
			$sql="select * from usermain as A, userdetail as B where A.id=B.id";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
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
			}
			
			require("closedb.php");
			
			return $arr;
		}
		
		public static function getUserById($_id){
			require("opendb.php");
			$arr=array();
			$sql="select * from usermain as A, userdetail as B where A.id=$_id and A.id=B.id";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			$u=NULL;
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
				break;
			}
			require("closedb.php");
			return $u;
		}
		/*
		public static function getUserByAddress($_addr){
			require("opendb.php");
			$arr=array();
			$_addr="+".str_replace(" ", " +",$_addr);
			//echo $_addr;
			$sql="SELECT * FROM userdetail as A, usermain as B WHERE MATCH (address) AGAINST ('$_addr' IN BOOLEAN MODE) and ispublic=1 and A.id=B.id;";
			$rs=mysql_query($sql);
			while($record=mysql_fetch_array($rs)){
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
				array_push($arr, $u);
			}
			return $arr;
		}
		*/
		public static function getMyProfile($_id){
			require("opendb.php");
			$arr=array();
			$sql="select * from usermain as A, userdetail as B where A.id=$_id and A.id=B.id";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			$u=NULL;
			while($record=mysql_fetch_array($rs)){
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
					break;
			}
			require("closedb.php");
			return $u;
		}
		
		
		public static function getUserTempByActivationCode($_id, $_email, $_code){
			require("opendb.php");
			$arr=array();
			$sql="select * from user_temp where activecode='$_code' and email='$_email' and id=$_id";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			$u=NULL;
			while($record=mysql_fetch_array($rs)){
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
				
				break;
			}
			require("closedb.php");
			return $u;
		}
		
		public static function getUserNumberByActivationCode($_id, $_email, $_code){
			require("opendb.php");
			$arr=array();
			$sql="select number from user_temp where activecode='$_code' and email='$_email' and id=$_id";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			$no="";
			while($record=mysql_fetch_array($rs)){
				$no=$record['number'];
				break;
			}
			require("closedb.php");
			return $no;
		}
		
		
		public static function getFogetTempByActivationCode($_id, $_email, $_code){
			require("opendb.php");
			$arr=array();
			$sql="select id from forget_temp where activecode='$_code' and email='$_email' and id=$_id";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			$u=0;
			while($record=mysql_fetch_array($rs)){
				$u=$record['id'];
			}
			require("closedb.php");
			return $u;
		}
		
		private static function getUserByName($_id, $_idx, $_name){
			$arr=array();
			$sql="select * from usermain as A, userdetail as B, (select count(*) as counter from friend where uid=$_id and fid=$_idx) as TB where TB.counter=0 and A.id=$_idx and A.id=B.id";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			$u=NULL;
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
				break;
			}
			if($u!=NULL)
			foreach($u as $key=>$value){
				if($_name===NULL || $_name=="") return $u;
				$s1=self::utf8_to_ascii2($value);
				$s2=self::utf8_to_ascii2($_name);
				if($key=="name" && strpos($s1, $s2)!==false){
					return $u;
					break;
				}
			}
			return NULL;
		}
		
		public static function getAllUsersByName($_id, $_name) {
			require("opendb.php");
			$arr1=array();
			$arr2=array();
			
			$sql="select id from usermain";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr1, $record['id']);
			}
			foreach($arr1 as $_idx){
				$u=UserManager::getUserByName($_id, $_idx, $_name);
				if($u!=NULL & $_id!=$_idx) array_push($arr2, $u);
			}
			
			require("closedb.php");
			
			return $arr2;
		}
		
		private static function getUserByAddress($_id, $_idx, $_addr){
			$sql="select * from usermain as A, userdetail as B, (select count(*) as counter from friend where uid=$_id and fid=$_idx) as TB where TB.counter=0 and A.id=$_idx and A.id=B.id and B.ispublic=1";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			$u=NULL;
			while($record=mysql_fetch_array($rs)){
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
				break;
			}
			if($u!=NULL)
			foreach($u as $key=>$value){
				if($_addr===NULL || $_addr=="") return $u;
				$s1=self::utf8_to_ascii2($value);
				$s2=self::utf8_to_ascii2($_addr);
				if($key=="address" && strpos($s1, $s2)!==false){
					return $u;
					break;
				}
			}
			return NULL;
		}
		
		public static function getAllUsersByAddress($_id, $_addr) {
			require("opendb.php");
			$arr1=array();
			$arr2=array();
			
			$sql="select id from usermain";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr1, $record['id']);
			}
			foreach($arr1 as $_idx){
				$u=UserManager::getUserByAddress($_id, $_idx, $_addr);
				if($u!=NULL & $_id!=$_idx) array_push($arr2, $u);
			}
			
			require("closedb.php");
			
			return $arr2;
		}
		
		
		
		private static function getUserBySchool($_id, $_idx, $_school){
			$arr=array();
			$sql="select * from usermain as A, userdetail as B, (select count(*) as counter from friend where uid=$_id and fid=$_idx) as TB where TB.counter=0 and A.id=$_idx and A.id=B.id and B.ispublic=1";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			$u=NULL;
			while($record=mysql_fetch_array($rs)){
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
				break;
			}
			if($u!=NULL)
			foreach($u as $key=>$value){
				if($_school===NULL || $_school=="") return $u;
				$s1=self::utf8_to_ascii2($value);
				$s2=self::utf8_to_ascii2($_school);
				if($key=="school" && strpos($s1, $s2)!==false){
					return $u;
					break;
				}
			}
			return NULL;
		}
		
		public static function getAllUsersBySchool($_id, $_school) {
			require("opendb.php");
			$arr1=array();
			$arr2=array();
			
			$sql="select id from usermain";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr1, $record['id']);
			}
			foreach($arr1 as $_idx){
				$u=UserManager::getUserBySchool($_id, $_idx, $_school);
				if($u!=NULL & $_id!=$_idx) array_push($arr2, $u);
			}
			
			require("closedb.php");
			
			return $arr2;
		}
		
		
		
		private static function getUserByWorkplace($_id, $_idx, $_wp){
			$arr=array();
			$sql="select * from usermain as A, userdetail as B, (select count(*) as counter from friend where uid=$_id and fid=$_idx) as TB where TB.counter=0 and A.id=$_idx and A.id=B.id and B.ispublic=1";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			$u=NULL;
			while($record=mysql_fetch_array($rs)){
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
				break;
			}
			if($u!=NULL)
			foreach($u as $key=>$value){
				if($_wp===NULL || $_wp=="") return $u;
				$s1=self::utf8_to_ascii2($value);
				$s2=self::utf8_to_ascii2($_wp);
				if($key=="workplace" && strpos($s1, $s2)!==false){
					return $u;
					break;
				}
			}
			return NULL;
		}
		
		public static function getAllUsersByWorkplace($_id, $_wp) {
			require("opendb.php");
			$arr1=array();
			$arr2=array();
			
			$sql="select id from usermain";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr1, $record['id']);
			}
			foreach($arr1 as $_idx){
				$u=UserManager::getUserByWorkplace($_id, $_idx, $_wp);
				if($u!=NULL & $_id!=$_idx) array_push($arr2, $u);
			}
			
			require("closedb.php");
			
			return $arr2;
		}
		
		private static function getUserBySthg($_id, $_idx, $_sthg){
			$arr=array();
			$sql="select * from usermain as A, userdetail as B, (select count(*) as counter from friend where uid=$_id and fid=$_idx) as TB where TB.counter=0 and A.id=$_idx and A.id=B.id";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			$u=NULL;
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
				break;
			}
			if($u!=NULL){
				if($_sthg===NULL || $_sthg=="") return $u;
				/*foreach($u as $key=>$value){
					$s1=self::utf8_to_ascii2($value);
					$s2=self::utf8_to_ascii2($_sthg);
					if(($key=="name" && strpos($s1, $s2)!==false) 
						|| ($key=="address" && strpos($s1, $s2)!==false)
						|| ($key=="school" && strpos($s1, $s2)!==false)
						|| ($key=="workplace" && strpos($s1, $s2)!==false)){
						return $u;
						break;
					}
				}*/
				$s=self::utf8_to_ascii2($_sthg);
				if((strpos(self::utf8_to_ascii2($u->name), $s)!==false)
					|| (strpos(self::utf8_to_ascii2($u->address), $s)!==false && $u->ispublic==1)
					|| (strpos(self::utf8_to_ascii2($u->school), $s)!==false && $u->ispublic==1)
					|| (strpos(self::utf8_to_ascii2($u->workplace), $s)!==false && $u->ispublic==1))
					return $u;
			}
			return NULL;
		}
		
		public static function getAllUsersBySthg($_id, $_sthg) {
			require("opendb.php");
			$arr1=array();
			$arr2=array();
			
			$sql="select id from usermain";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr1, $record['id']);
			}
			foreach($arr1 as $_idx){
				$u=UserManager::getUserBySthg($_id, $_idx, $_sthg);
				if($u!=NULL & $_id!=$_idx) array_push($arr2, $u);
			}
			
			require("closedb.php");
			
			return $arr2;
		}
		
		
		private static function getUserByGender($_id, $_idx, $_gender){
			$arr=array();
			$sql="select * from usermain as A, userdetail as B, (select count(*) as counter from friend where uid=$_id and fid=$_idx) as TB where TB.counter=0 and A.id=$_idx and A.id=B.id and B.ispublic=1 and gender=$_gender";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			$u=NULL;
			while($record=mysql_fetch_array($rs)){
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
				break;
			}
			return $u;
		}
		
		public static function getAllUsersByGender($_id, $_gender) {
			require("opendb.php");
			$arr1=array();
			$arr2=array();
			
			$sql="select id from usermain";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr1, $record['id']);
			}
			foreach($arr1 as $_idx){
				$u=UserManager::getUserByGender($_id, $_idx, $_gender);
				if($u!=NULL & $_id!=$_idx) array_push($arr2, $u);
			}
			
			require("closedb.php");
			
			return $arr2;
		}
		
		
		private static function getUserByYears($_id, $_idx, $_from, $_to){
			$arr=array();
			$sql="select * from usermain as A, userdetail as B, (select count(*) as counter from friend where uid=$_id and fid=$_idx) as TB where TB.counter=0 and A.id=$_idx and A.id=B.id and B.ispublic=1 and (YEAR(birthday) BETWEEN $_from AND $_to)";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			$u=NULL;
			while($record=mysql_fetch_array($rs)){
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
				break;
			}
			return $u;
		}
		
		
		public static function getEmailByNumber($_number){
			require("opendb.php");
			$e="";
			$sql="select email from userdetail as A, account as B where A.id=B.id and B.number='$_number'";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			$u=NULL;
			while($record=mysql_fetch_array($rs)){
				$e=$record['email'];
				break;
			}
			require("closedb.php");
			return $e;
		}
		
		public static function getAllUsersByYears($_id, $_from, $_to) {
			require("opendb.php");
			$arr1=array();
			$arr2=array();
			
			$sql="select id from usermain";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr1, $record['id']);
			}
			foreach($arr1 as $_idx){
				$u=UserManager::getUserByYears($_id, $_idx, $_from, $_to);
				if($u!=NULL & $_id!=$_idx) array_push($arr2, $u);
			}
			
			require("closedb.php");
			
			return $arr2;
		}
		
		//advance
		
		private static function getUserByAdvance($_id, $_idx, $_name, $_gender, $_addr, $_yearfrom, $_yearto, $_school, $_workplace){
			$arr=array();
			$sql="select * from usermain as A, userdetail as B, (select count(*) as counter from friend where uid=$_id and fid=$_idx) as TB where TB.counter=0 and A.id=$_idx and A.id=B.id and B.ispublic=1";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			$u=NULL;
			while($record=mysql_fetch_array($rs)){
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
				break;
			}
			if($u!=NULL)
			foreach($u as $key=>$value){
				$s1=self::utf8_to_ascii2($value);
				$sname=self::utf8_to_ascii2($_name);
				$swp=self::utf8_to_ascii2($_workplace);
				$saddr=self::utf8_to_ascii2($_addr);
				$sschool=self::utf8_to_ascii2($_school);
				
				if($key=="name" && !(strpos($s1, $sname)!==false)){
					echo "<br />".$key."=".$value."!=".$sname;
					return NULL;
				}
				if($key=="school" && !(strpos($s1, $sschool)!==false)){
					echo "<br />".$key."=".$value."!=".$sschool;
					return NULL;
				}
				if($key=="address" && !(strpos($s1, $saddr)!==false)){
					echo "<br />".$key."=".$value."!=".$saddr;
					return NULL;
				}
				if($key=="workplace" && !(strpos($s1, $swp)===false)){
					echo "<br />".$key."=".$value."!=".$swp;
					return NULL;
				}
				if($key=="gender" && !($_gender===NULL || $_gender=="" || $_gender==$value)){
					echo "<br />".$key."=".$value."!=".$_gender;
					return NULL;
				}
				if($key=="birthday" && !(date("Y", strtotime($value))>=$_yearfrom && date("Y", strtotime($value))<=$_yearto) || $_yearfrom===NULL || $_yearfrom=="" || $_yearto===NULL || $_yearto=="" ){
					echo "<br />".$key."=".$value."!=".$_yearfrom.$_yearto;
					return NULL;
				}
			}
			return $u;
		}
		
		public static function getAllUsersByAdvance($_id, $_name, $_gender, $_addr, $_yearfrom, $_yearto, $_school, $_workplace) {
			require("opendb.php");
			$arr1=array();
			$arr2=array();
			
			$sql="select id from usermain";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr1, $record['id']);
			}
			foreach($arr1 as $_idx){
				$u=UserManager::getUserByAdvance($_id, $_idx, $_name, $_gender, $_addr, $_yearfrom, $_yearto, $_school, $_workplace);
				if($u!=NULL & $_id!=$_idx) array_push($arr2, $u);
			}
			
			require("closedb.php");
			
			return $arr2;
		}
		
		public static function getUserTemp($_id, $_email, $_activecode)
		{
			$arr=array();
			require("opendb.php");
			
			$sql="select * from user_temp where email='$_email' and activecode='$_activecode' and id=$_id";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
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
				array_push($arr, $u);
				break;
			}
			
			require("closedb.php");
			
			return $arr[0];
		}
		
		public static function getUsersTempByNumber($_number)
		{
			$arr=array();
			require("opendb.php");
			
			$sql="select * from user_temp where number='$_number'";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
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
				array_push($arr, $u);
			}
			
			require("closedb.php");
			
			return $arr;
		}
		
		
		public static function updateUserActiveCode($_id, $_code){
			require("opendb.php");
			
			$sql="update user_temp set activecode='$_code' where id=$_id";
			mysql_query($sql) or die("Query error: ".mysql_error($con));
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
		public static function getGcmIdById($_id){
			$arr=array();
			require("opendb.php");
			
			$sql="select gcmid from usermain where id=$_id";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr, $record['gcmid']);
				break;
			}
			
			require("closedb.php");
			
			return $arr[0];
		}
		
		public static function getAvailable($_id)
		{
			$arr=array();
			require("opendb.php");
			
			$sql="select count(*) as counter from usermain where id=$_id and TIMESTAMPDIFF(MINUTE,lastlogin,NOW())<2;";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				array_push($arr, $record['counter']);
				break;
			}
			
			require("closedb.php");
			return $arr[0];
		}
		
		
		private static function checkPassword($_id, $_pass){
			$arr=array();
			
			$sql="select count(*) as counter from userlogin where id=$_id and password='$_pass'";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			
			while($record=mysql_fetch_array($rs)){
				array_push($arr, $record['counter']);
				break;
			}
			return $arr[0]>0;
		}
		
		
		public static function changePassword($_id, $_oldpass, $_newpass){
			require("opendb.php");
			if(UserManager::checkPassword($_id, $_oldpass)){
				$sql="update userlogin set password='$_newpass' where id=$_id";
				mysql_query($sql) or die("Query error: ".mysql_error($con));
						
				$rs=mysql_affected_rows();
				require("closedb.php");
				return $rs;
			}
			require("closedb.php");
			return 0;
		}
		
		
		public static function changePasswordByEmail($_id, $_newpass){
			require("opendb.php");
			$sql="update userlogin set password='$_newpass' where id=$_id";
			mysql_query($sql) or die("Query error: ".mysql_error($con));
					
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
			require("closedb.php");
			return 0;
		}
		
		private static function getIdByNumberWithoutFriend($_id, $_number){
			$arr=array();
			$sql="select B.id from usermain as A, account as B, (select count(*) as counter from friend as FR, account as AC where FR.uid=$_id and FR.fid=AC.id and AC.number=$_number) as TB where TB.counter=0 and A.id=B.id and B.number=$_number";
			$rs=mysql_query($sql) or die("Query error: ".mysql_error($con));
			
			if($rs!=NULL){
				while($record=mysql_fetch_array($rs)){
				array_push($arr, $record['id']);
				break;
				}
				return $arr[0];
			}
			return NULL;
		}
		
		public static function getNewUserIdListByNumberList($_id, $_numberlist){
			require("opendb.php");
			$pieces = explode(".", $_numberlist);
			$arr=array();
			foreach($pieces as $number){
				$_idz=UserManager::getIdByNumberWithoutFriend($_id, $number);
				if($_idz!=NULL & $_id!=$_idz) array_push($arr, array($number, $_idz));
			}
			require("closedb.php");
			return $arr;
		}
		/*
		static function utf8_to_ascii1($str){
			$unicode = array(
				'a'=>'á|à|ả|ã|ạ|ă|ắ|ặ|ằ|ẳ|ẵ|â|ấ|ầ|ẩ|ẫ|ậ',
				'd'=>'đ',
				'e'=>'é|è|ẻ|ẽ|ẹ|ê|ế|ề|ể|ễ|ệ',
				'i'=>'í|ì|ỉ|ĩ|ị',
				'o'=>'ó|ò|ỏ|õ|ọ|ô|ố|ồ|ổ|ỗ|ộ|ơ|ớ|ờ|ở|ỡ|ợ',
				'u'=>'ú|ù|ủ|ũ|ụ|ư|ứ|ừ|ử|ữ|ự',
				'y'=>'ý|ỳ|ỷ|ỹ|ỵ',
				'A'=>'Á|À|Ả|Ã|Ạ|Ă|Ắ|Ặ|Ằ|Ẳ|Ẵ|Â|Ấ|Ầ|Ẩ|Ẫ|Ậ',
				'D'=>'Đ',
				'E'=>'É|È|Ẻ|Ẽ|Ẹ|Ê|Ế|Ề|Ể|Ễ|Ệ',
				'I'=>'Í|Ì|Ỉ|Ĩ|Ị',
				'O'=>'Ó|Ò|Ỏ|Õ|Ọ|Ô|Ố|Ồ|Ổ|Ỗ|Ộ|Ơ|Ớ|Ờ|Ở|Ỡ|Ợ',
				'U'=>'Ú|Ù|Ủ|Ũ|Ụ|Ư|Ứ|Ừ|Ử|Ữ|Ự',
				'Y'=>'Ý|Ỳ|Ỷ|Ỹ|Ỵ',
			);
        
		   foreach($unicode as $nonUnicode=>$uni){
				$str = preg_replace("/($uni)/i", $nonUnicode, $str);
		   }
			return $str;
    	}*/
		/* ------------- UTF8 to Ascii ------------- */
		static function utf8_to_ascii2($str){
			if($str==NULL || !isset($str) || $str=="") return "";
			$unicode = array(
			'a'	=>	'A|À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ|à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ',
			'b' =>  'B',
			'c' =>  'C',
			'd'	=>	'D|Đ|đ',
			'e' =>	'E|É|È|Ẻ|Ẽ|Ẹ|Ê|Ế|Ề|Ể|Ễ|Ệ|é|è|ẻ|ẽ|ẹ|ê|ế|ề|ể|ễ|ệ',
			'f'	=>	'F',
			'g'	=>	'G',
			'h'	=>	'H',
			'i'	=>	'I|Í|Ì|Ỉ|Ĩ|Ị|í|ì|ỉ|ĩ|ị',	
			'j'	=>	'J',
			'k'	=>	'K',
			'l'	=>	'L',
			'm'	=>	'M',
			'n'	=>	'N',
			'o'	=>	'O|Ó|Ò|Ỏ|Õ|Ọ|Ô|Ố|Ồ|Ổ|Ỗ|Ộ|Ơ|Ớ|Ờ|Ở|Ỡ|Ợ|ó|ò|ỏ|õ|ọ|ô|ố|ồ|ổ|ỗ|ộ|ơ|ớ|ờ|ở|ỡ|ợ',
			'p'	=>	'P',
			'q'	=>	'Q',
			'r'	=>	'R',
			's'	=>	'S',
			't'	=>	'T',
			'u'	=>	'U|Ú|Ù|Ủ|Ũ|Ụ|Ư|Ứ|Ừ|Ử|Ữ|Ự|ú|ù|ủ|ũ|ụ|ư|ứ|ừ|ử|ữ|ự',
			'v'	=>	'V',
			'w'	=>	'W',
			'x'	=>	'X',
			'y'	=>	'Y|Ý|Ỳ|Ỷ|Ỹ|Ỵ|ý|ỳ|ỷ|ỹ|ỵ',
			'z'	=>	'Z',
			' '	=>	'-'	);
			foreach($unicode as $nonUnicode=>$uni) $str = preg_replace("/($uni)/i",$nonUnicode,$str);
			return $str;
		}
		/*
		static function utf8_to_ascii3($str){
			$str = preg_replace("/(à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ)/", 'a', $str);
			$str = preg_replace("/(è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ)/", 'e', $str);
			$str = preg_replace("/(ì|í|ị|ỉ|ĩ)/", 'i', $str);
			$str = preg_replace("/(ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ)/", 'o', $str);
			$str = preg_replace("/(ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ)/", 'u', $str);
			$str = preg_replace("/(ỳ|ý|ỵ|ỷ|ỹ)/", 'y', $str);
			$str = preg_replace("/(đ)/", 'd', $str);
			$str = preg_replace("/(À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ)/", 'A', $str);
			$str = preg_replace("/(È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ)/", 'E', $str);
			$str = preg_replace("/(Ì|Í|Ị|Ỉ|Ĩ)/", 'I', $str);
			$str = preg_replace("/(Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ)/", 'O', $str);
			$str = preg_replace("/(Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ)/", 'U', $str);
			$str = preg_replace("/(Ỳ|Ý|Ỵ|Ỷ|Ỹ)/", 'Y', $str);
			$str = preg_replace("/(Đ)/", 'D', $str);
			//$str = str_replace(" ", "-", str_replace("&amp;*#39;","",$str));
			return $str;
		}
		
		static function utf8_to_ascii4($str){
			$str = preg_replace("/(à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ|À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ)/", 'a', $str);
			$str = preg_replace("/(è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ|È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ)/", 'e', $str);
			$str = preg_replace("/(ì|í|ị|ỉ|ĩ|Ì|Í|Ị|Ỉ|Ĩ)/", 'i', $str);
			$str = preg_replace("/(ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ|Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ)/", 'o', $str);
			$str = preg_replace("/(ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ|Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ)/", 'u', $str);
			$str = preg_replace("/(ỳ|ý|ỵ|ỷ|ỹ|Ỳ|Ý|Ỵ|Ỷ|Ỹ)/", 'y', $str);
			$str = preg_replace("/(đ|Đ)/", 'd', $str);
			//$str = str_replace(" ", "-", str_replace("&amp;*#39;","",$str));
			return $str;
		}
		*/
		/*
		static function utf8_to_ascii5($str){
			$marTViet=array("à","á","ạ","ả","ã","â","ầ","ấ","ậ","ẩ","ẫ","ă",
			"ằ","ắ","ặ","ẳ","ẵ","è","é","ẹ","ẻ","ẽ","ê","ề",
			"ế","ệ","ể","ễ",
			"ì","í","ị","ỉ","ĩ",
			"ò","ó","ọ","ỏ","õ","ô","ồ","ố","ộ","ổ","ỗ","ơ",
			"ờ","ớ","ợ","ở","ỡ",
			"ù","ú","ụ","ủ","ũ","ư","ừ","ứ","ự","ử","ữ",
			"ỳ","ý","ỵ","ỷ","ỹ",
			"đ",
			"À","Á","Ạ","Ả","Ã","Â","Ầ","Ấ","Ậ","Ẩ","Ẫ","Ă",
			"Ằ","Ắ","Ặ","Ẳ","Ẵ",
			"È","É","Ẹ","Ẻ","Ẽ","Ê","Ề","Ế","Ệ","Ể","Ễ",
			"Ì","Í","Ị","Ỉ","Ĩ",
			"Ò","Ó","Ọ","Ỏ","Õ","Ô","Ồ","Ố","Ộ","Ổ","Ỗ","Ơ","Ờ","Ớ","Ợ","Ở","Ỡ",
			"Ù","Ú","Ụ","Ủ","Ũ","Ư","Ừ","Ứ","Ự","Ử","Ữ",
			"Ỳ","Ý","Ỵ","Ỷ","Ỹ",
			"Đ"," ");
			 */
			/*Mảng chứa tất cả ký tự không dấu tương ứng với mảng $marTViet bên trên*/
			/*$marKoDau=array("a","a","a","a","a","a","a","a","a","a","a",
			"a","a","a","a","a","a",
			"e","e","e","e","e","e","e","e","e","e","e",
			"i","i","i","i","i",
			"o","o","o","o","o","o","o","o","o","o","o","o",
			"o","o","o","o","o",
			"u","u","u","u","u","u","u","u","u","u","u",
			"y","y","y","y","y",
			"d",
			"A","A","A","A","A","A","A","A","A","A","A","A",
			"A","A","A","A","A",
			"E","E","E","E","E","E","E","E","E","E","E",
			"I","I","I","I","I",
			"O","O","O","O","O","O","O","O","O","O","O","O","O","O","O","O","O",
			"U","U","U","U","U","U","U","U","U","U","U",
			"Y","Y","Y","Y","Y",
			"D","-");
			 
			if ($tolower) {
				return strtolower(str_replace($marTViet,$marKoDau,$str));
			}
			 
			return str_replace($marTViet,$marKoDau,$str);
			 
		}*/
	}
	
	
?>