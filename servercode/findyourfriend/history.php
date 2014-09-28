<?php
	class History{
		
		//attributes
		public $timest;
		public $latitude;
		public $longtitude;
		
		public function History($_timest, $_latitude, $_longtitude){
			$this->timest=$_timest;
			$this->longtitude=$_longtitude;
			$this->latitude=$_latitude;
		}
	}
	
	class HistoryManager{
		
		public static function getAllUserHistory(){
			$arr=array();
			require("opendb.php");
			
			$sql="select * from history order by timest desc";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				$u=new History(
							$record['timest'],
							$record['latitude'],
							$record['longtitude']
							);
				array_push($arr, $u);
			}
			
			require("closedb.php");
			
			return $arr;
		}
		public static function getUserHistory($_id){
			$arr=array();
			require("opendb.php");
			
			$sql="select * from history where id=$_id order by timest desc limit 10";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				$u=new History(
							$record['timest'],
							$record['latitude'],
							$record['longtitude']
							);
				array_push($arr, $u);
			}
			
			require("closedb.php");
			
			return $arr;
		}
		
		
		public static function getLastUserHistory($_id){
			$u=NULL;
			require("opendb.php");
			
			$sql="select * from history where id=$_id order by timest desc limit 1";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				$u=new History(
							$record['timest'],
							$record['latitude'],
							$record['longtitude']
							);
				break;
			}
			
			require("closedb.php");
			
			return $u;
		}
		
		public static function addNewHistory($_id, $_history){
			require("opendb.php");
			
			$u=self::getLastUserHistory($_id);
			$rs=0;
			require("opendb.php");
			
			if($u->latitude!=$_history->latitude || $u->longtitude!=$_history->longtitude || $u===NULL){
				$sql="insert into history values($_id, NOW(), '$_history->latitude', '$_history->longtitude')";
				mysql_query($sql) or die("Query error: ".mysql_error($con));
				$rs=mysql_affected_rows();
			}
			else{
				$sql="update history set timest=NOW() where id=$_id and latitude=$u->latitude and longtitude=$u->longtitude and timest='$u->timest'";
				mysql_query($sql) or die("Query error: ".mysql_error($con));
				$rs=mysql_affected_rows();
			}
			
			// delete the oldest history
			$counter=0;
			$sql="select count(*) as counter from history where id=$_id";
			$r=mysql_query($sql) or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($r)){
				$counter=$record['counter'];
			}
			if($counter>10){
				$sql="delete from history where id=$_id order by timest asc limit 1";
				mysql_query($sql) or die("Query error: ".mysql_error($con));
				$rs=mysql_affected_rows();
			}
			
			require("closedb.php");
			return $rs;
		}
		
		public static function removeHistory($_id){
			require("opendb.php");
			
			$sql="delete from history where id=$_id";
			mysql_query($sql) or die("Query error: ".mysql_error($con));
			$rs=mysql_affected_rows();
			require("closedb.php");
			return $rs;
		}
		
	}
?>