<?php 

	define("GOOGLE_API_KEY", "AIzaSyAwQ_kyH9uxISFtW9iENbA-25pZhH6F2t8");

// storing message
	function storeMessage($fromaddr, $toaddr, $message) {
		echo "store message";
		require("opendb.php");
		$result = mysql_query(
			"INSERT INTO gcm_messages
			(fromaddr, toaddr, message)
			VALUES ('$fromaddr', '$toaddr', '$message')");
			
		if ($result) {
			$id = mysql_insert_id();
			$result = mysql_query(
				"SELECT * 
					FROM gcm_messages
					WHERE id = $id") or die(mysql_error());
					
			// return 
			if (mysql_num_rows($result) > 0) {
				return mysql_fetch_array($result);
			} else {
				return false;
			} 
		} else {
			return false;
		}

	}

// storing location
	function storeLocation($latitute, $longtitute, $gcm_regid) {
		require("opendb.php");
		$result = mysql_query(
			"INSERT INTO gcm_locations
			(latitute, longtitute, gcm_regid, create_at)
			VALUES ('$latitute', '$longtitute', '$gcm_regid', NOW())");
			
		if ($result) {
			$id = mysql_insert_id();
			$result = mysql_query(
				"SELECT *
					FROM gcm_locations
					WHERE id = $id") or die(mysql_error());
			
			// return location details
			if (mysql_num_rows($result) > 0) {
				return mysql_fetch_array($result);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}


// get all locations
	function getAllLocations() {
		require("opendb.php");
		$result = mysql_query("select * 
                                    FROM gcm_locations");
		require("closedb.php");
        	return $result;
	}

// storing new user and return user detail
	function storeUser($username, $email, $gcm_regid) {
		// insert user into database
		require("opendb.php");
		$result = mysql_query(
			"INSERT INTO gcm_users
			(name, email, gcm_regid, create_at)
			VALUES ('$name', '$email', '$gcm_regid', NOW())");
		
		
		if ($result) {
			// get user details
			$id = mysql_insert_id();
			$result = mysql_query(
				"SELECT *
					FROM gcm_users
					WHERE id = $id") or die(mysql_error());
					
			// return user details
			if (mysql_num_rows($result) > 0) {
				return mysql_fetch_array($result);
			} else {
				return false;
			} 
		} else {
			return false;
		}
	}

	function getUserByEmail($email) {
		require("opendb.php");
		$result = mysql_query("SELECT * 
                                    FROM gcm_users 
                                    WHERE email = '$email'
                                    LIMIT 1");
		require("closedb.php");
        return $result;
	}
	
	function getAllUsers() {
		require("opendb.php");
		$result = mysql_query("select * 
                                    FROM gcm_users");
		require("closedb.php");
        return $result;
	}
	
	function isUserExisted($email) {
		require("opendb.php");
		$result = mysql_query("SELECT email 
                                       from gcm_users 
                                       WHERE email = '$email'");
                                        
        $NumOfRows = mysql_num_rows($result);
        require("closedb.php");
        if ($NumOfRows > 0) {
            // user existed
            return true;
        } else {
            // user not existed
            return false;
        }
	}
	
	function send_push_notification($registration_ids, $message) {
		// Set POST variables
		require("opendb.php");
        $url = 'https://android.googleapis.com/gcm/send';
 
        $fields = array(
            'registration_ids' => $registration_ids,
            'data' => $message,
        );
 
        $headers = array(
            'Authorization: key=' . GOOGLE_API_KEY,
            'Content-Type: application/json'
        );
        //print_r($headers);
        // Open connection
        $ch = curl_init();
 
        // Set the url, number of POST vars, POST data
        curl_setopt($ch, CURLOPT_URL, $url);
 
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
 
        // Disabling SSL Certificate support temporarly
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
 
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
 
        // Execute post
        $result = curl_exec($ch);
        if ($result === FALSE) {
            die('Curl failed: ' . curl_error($ch));
        }
 
        // Close connection
        curl_close($ch);
        require("closedb.php");
        return $result;
	}
	
?>
