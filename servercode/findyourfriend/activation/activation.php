<?php
include '../user.php';
include '../account.php';
$msg='';
if(!empty($_GET['id']) && isset($_GET['id']) && !empty($_GET['code']) && isset($_GET['code']) && !empty($_GET['email']) && isset($_GET['email']))
{
	$id=$_GET['id'];
	$code=$_GET['code'];
	$email=$_GET['email'];

	$u=UserManager::getUserTempByActivationCode($id, $email, $code);
	$rs=0;
	if($u!=NULL){
		$number=UserManager::getUserNumberByActivationCode($id, $email, $code);
		if(AccountManager::createNewAccount($id, $number)>0){
			$rs=UserManager::createNewUser($u);
			UserManager::activeUserLogin($u->id);
		}
		else{
			$msg="Số điện thoại đã được sử dụng! Tài khoản sẽ bị hủy bỏ!";
		}
		UserManager::removeUserTemp($id, $email, $code);
	}
	
	if($rs>0){
		if($msg==='') $msg="Kích hoạt thành công!";
	}
	else{
		if($msg==='') $msg="Tài khoản đã được kích hoạt hoặc mã xác nhận sai! Vui lòng kiểm tra lại email!";
	}

}
else{
	if($msg!=="") $msg="Truy cập không hợp lệ!";
}
?>
<!doctype html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<title>Xác nhân đăng ký</title>
<link rel="stylesheet" href="<?php echo $base_url; ?>style.css"/>
<link rel="shortcut icon" href="http://sgulab.letsgeekaround.com/img/logo_small.png">
</head>
<body style="font-family:Arial, Helvetica, sans-serif; padding:0px; margin:0px;" >
	<div style="background:#15cef6; color:#FFF; padding:2px; font-family:Verdana, Geneva, sans-serif; font-size:2ex;">
    	<img src="../img/logo.png" align=ABSMIDDLE width="35"/>SGU LAB
    </div>
    <div id="main" style="margin-top:80px; background-color:#15cef6; width:350px; height:350px; margin-left:auto; margin-right:auto; text-align:center; border:solid 2px #15cef6; border-radius:20px;color:#111; padding:20px">
        <h1 style="font-size:3.5ex;">FIND YOUR FRIEND</h1>
    	<div><img src="../img/logo.png"  /></div>
        <h2 style="margin-top:50px; font-size:2ex; font-weight:normal"><?php echo $msg; ?></h2>
    </div>
</body>
</html>
