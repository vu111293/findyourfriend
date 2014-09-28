<?php
	class AppManager{
		public static $appcode="AHC99-97JA8-AK009-AIJD1";
		public static function checkAppCode($_appcode){
			return $_appcode==AppManager::$appcode;
		}
	}
?>