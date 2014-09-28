<?php
	class Location{
		
		//attributes
		public $lat;
		public $lng;
		public $street;
		public $sublocality;
		public $locality;
		public $administrative_area;
		public $country;
		public $fulladdr;
		
		public function Location($_lat, $_lng, $_street, $_sublocality,$_locality,$_administrative_area,$_country,$_fulladdr){
			$this->street=$_street;
			$this->sublocality=$_sublocality;
			$this->locality=$_locality;
			$this->administrative_area=$_administrative_area;
			$this->country=$_country;
			$this->fulladdr=$_fulladdr;
			$this->lat=$_lat;
			$this->lng=$_lng;
		}
	}
	
	class LocationManager{
		
		public static function getAllLocationData(){
			$arr=array();
			require("opendb.php");
			
			$sql="select * from location order by id desc";
			$rs=mysql_query($sql);// or die("Query error: ".mysql_error($con));
			while($record=mysql_fetch_array($rs)){
				$l=new Location(
							$record['lat'],
							$record['lng'],
							$record['street'],
							$record['sublocality'],
							$record['locality'],
							$record['administrative_area'],
							$record['country'],
							$record['fulladdr']
							);
				array_push($arr, $l);
			}
			
			require("closedb.php");
			
			return $arr;
		}
		
		public static function updateNewLocation($_id, $_lat, $_lng){
			require("opendb.php");
			
			$_loc=self::locationParse($_lat, $_lng);
			
			$sql="select id from location where id=$_id";
			$rs=mysql_query($sql);
			if($aa=mysql_fetch_array($rs)){
				$sql="update location set lat='$_loc->lat', lng='$_loc->lng', street='$_loc->street', sublocality='$_loc->sublocality', locality='$_loc->locality', administrative_area='$_loc->administrative_area', country='$_loc->country', fulladdr='$_loc->fulladdr' where id=$_id";
				mysql_query($sql);// or die("Query error: ".mysql_error($con));
			}
			else{
				$sql="insert into location values($_id, '$_loc->lat','$_loc->lng', '$_loc->street', '$_loc->sublocality', '$_loc->locality', '$_loc->administrative_area', '$_loc->country', '$_loc->fulladdr')";
				mysql_query($sql);
			}
			$rs=mysql_affected_rows();
			require("closedb.php");
			
			return $rs;
		}
		
		public static function locationParse($lat, $lng){
			$location_arr=json_decode(self::phpGetLocation($lat, $lng),true);
			$location=$location_arr["results"][0]["address_components"];
			$ii=count($location);
			$_fulladdr=$location_arr["results"][0]["formatted_address"];
			$_street=self::getAddressComponent($location, "street_number")." ".self::getAddressComponent($location, "route");
			$_sublocality=self::getAddressComponent($location, "sublocality");
			$_locality=self::getAddressComponent($location, "locality");
			if($_locality=="") $_locality=self::getAddressComponent($location, "administrative_area_level_2");
			$_administrative_area=self::getAddressComponent($location, "administrative_area_level_1");
			$_country=self::getAddressComponent($location, "country");
			
			if(is_numeric($_sublocality)) $_sublocality="Phường ".$_sublocality;
			if(is_numeric($_locality)) $_locality="Quận ".$_locality;
			
			$_street=str_replace("Unnamed Road","",$_street);
			$_street=str_replace("QL","Quốc lộ",$_street);
			$_street=str_replace("TL","Tỉnh lộ̣",$_street);
			$_administrative_area=str_replace(" Province","",$_administrative_area);
			$_locality=str_replace(" District","",$_locality);
			$l=new Location($lat, $lng, $_street, $_sublocality, $_locality, $_administrative_area, $_country, $_fulladdr);
			return $l;
		}
		
		public static function getUserInDistance($_lat, $_lng, $_distance){
			require("opendb.php");
			require("lib/Requests.php");
			Requests::register_autoloader();
			$idarr=array();
			$returnarr=array();
			$sql="select id, lat, lng from location";
			$rs=mysql_query($sql);
			while($record=mysql_fetch_array($rs)){
				array_push($idarr, array("id"=>$record['id'],"lat"=>$record['lat'],"lng"=>$record['lng']));
			}
			
			foreach($idarr as $e){
				$_dist=self::phpGetDistance($_lat, $_lng, $e['lat'], $e['lng']);
				if(is_numeric($_dist) && $_dist<$_distance){
					array_push($returnarr, $e);
				}
			}
			require("closedb.php");
			
			return $returnarr;
		}
		
		public static function getUserInCurAddress($_addr){
			require("opendb.php");
			$arr=array();
			$_addr="+".str_replace(" ", " +",$_addr);
			//echo $_addr;
			$sql="SELECT * FROM location WHERE MATCH (street, sublocality, locality, administrative_area, country) AGAINST ('$_addr' IN BOOLEAN MODE);";
			$rs=mysql_query($sql);
			while($record=mysql_fetch_array($rs)){
				array_push($arr, array("id"=>$record['id'],"lat"=>$record['lat'],"lng"=>$record['lng']));
			}
			require("closedb.php");
			return $arr;
		}
		
		private static function getAddressComponent($addr, $type){
			if($addr!="" && $addr!=NULL){
				foreach($addr as $cpn){
					foreach($cpn as $key => $value){
					if($key=="types" && in_array($type, $value))
						return $cpn["long_name"];
					}
				}
			}
			return "";
		}
		
		private static function phpGetLocation($lat, $lng){
			require("lib/Requests.php");
			Requests::register_autoloader();
			$respone=Requests::get("http://maps.googleapis.com/maps/api/geocode/json?latlng=$lat,$lng&sensor=true")->body;
			return $respone;
		}
		
		private static function getDistanceComponent($addr, $type){
			if($addr!="" && $addr!=NULL){
				foreach($addr as $cpn){
					foreach($cpn as $key => $value){
					if($key=="types" && in_array($type, $value))
						return $cpn["long_name"];
					}
				}
			}
			return "";
		}
		
		private static function phpGetDistance($lat1, $lng1, $lat2, $lng2){
			/*$respone=Requests::get("http://maps.googleapis.com/maps/api/distancematrix/json?origins=$lat1,$lng1&destinations=$lat2,$lng2")->body;
			$arr=json_decode($respone, true);
			echo "<br />".$respone;
			return $arr["rows"][0]["elements"][0]["distance"]["value"];*/
			return self::distance($lat1, $lng1, $lat2, $lng2, "K");
		}
		
		private static function distance($lat1, $lon1, $lat2, $lon2, $unit) {
 
		  	$theta = $lon1 - $lon2;
		  	$dist = sin(deg2rad($lat1)) * sin(deg2rad($lat2)) +  cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * cos(deg2rad($theta));
		  	$dist = acos($dist);
		  	$dist = rad2deg($dist);
		  	$miles = $dist * 60 * 1.1515;
		  	$unit = strtoupper($unit);
		
		  	if ($unit == "K") {
				return ($miles * 1.609344);
		  	} else if ($unit == "N") {
			  	return ($miles * 0.8684);
			} else {
				return $miles;
			}
		}
		
	}
?>