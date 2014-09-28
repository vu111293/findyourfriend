<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Test</title>
</head>

<body style="font-size:4ex; font-family:'Trebuchet MS', Arial, Helvetica, sans-serif; color:#C06">
	<?php
		require("user.php");
		print_r(UserManager::getAllUsersByYears($_GET['id'], $_GET['f'], $_GET['t']));
	?>
</body>
</html>