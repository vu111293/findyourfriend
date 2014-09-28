<?php
include '../user.php';
$msg='';
$_id=0;
$_ok=0;
if(!empty($_GET['id']) && isset($_GET['id']) &&!empty($_GET['code']) && isset($_GET['code']) && !empty($_GET['email']) && isset($_GET['email']))
{
	$id=$_GET['id'];
	$password=$_GET['password'];
	$code=$_GET['code'];
	$code=$_GET['code'];
	$email=$_GET['email'];

	$_id=UserManager::getFogetTempByActivationCode($id, $email, $code);
	$rs=0;
	if($_id==0){
		$msg='Truy cập không hợp lệ!';
	}
}
else{
	$msg='Truy cập không hợp lệ!';
}

if($_id!=0 && !empty($_POST['pass1']) && isset($_POST['pass1']) && !empty($_POST['pass2']) && isset($_POST['pass2'])){
	$pass1=$_POST['pass1'];
	$pass2=$_POST['pass2'];
	if($pass1!=$pass2){
		$msg='Mật khẩu nhập lại không khớp! Vui lòng nhập lại';
	}
	else{
		UserManager::changePasswordByEmail($_id, md5($pass1));
		UserManager::removeAllFogetTemp($_id,$email);
		$msg='Đổi mật khẩu thành công!';
		$_ok=1;
	}
}
?>
<!doctype html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<title>Đổi mật khẩu</title>
<link rel="stylesheet" href="<?php echo $base_url; ?>style.css"/>
<link rel="shortcut icon" href="http://sgulab.letsgeekaround.com/img/logo_small.png">
</head>
<body style="font-family:Arial, Helvetica, sans-serif; padding:0px; margin:0px;" bgcolor="#EEE" >
	<div style="background:#15cef6; color:#000; padding:2px; font-family:Verdana, Geneva, sans-serif; font-size:2ex;">
    	<img src="../img/logo.png" align=ABSMIDDLE width="35"/>SGU LAB
    </div>
    <div id="main" style="margin-top:80px;background-color:#15cef6; width:350px; height:350px; margin-left:auto; margin-right:auto; text-align:center; border:solid 2px #15cef6; border-radius:20px;color:#111; padding:20px">
        <h1 style="font-size:3.5ex">FIND YOUR FRIEND</h1>
    	<div><img src="../img/logo.png"  /></div>
		<?php 
			if($_id!=0 && $_ok==0){
				echo "
					<form method='POST'>
						<input type='password' name='pass1' style='border:1px solid; margin:5px; padding:5px; font-size:2ex;border-radius:5px' size='25' maxlength='30' placeholder='Mật khẩu mới' /><br />
						<input type='password' name='pass2' style='border:1px solid; margin:5px; padding:5px; font-size:2ex; border-radius:5px'  size='25' maxlength='30' placeholder='Nhập lại mật khẩu' /><br />
						<input type='submit' style='margin:10px; padding:2px; font-size:2ex; border-radius:5px' value='Đổi mật khẩu' />
					 </form>
				";
				echo $msg;
			}
			else echo "<h2 style='margin-top:50px; font-size:2ex; font-weight:normal'>$msg</h2>";
		 ?>
         <form>
         	
         </form>
    </div>
</body>
</html>
