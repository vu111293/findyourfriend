<?php
function sendVerifyPwd($_id, $_email, $_code){
	$rs=0;
	include 'smtp/Send_Mail.php';
	$to=$_email;
	$subject="FindYourFriend - Quên mật khẩu";
	$body="<body style='font-family:arial'>
	<div style='width:680px; height:auto; border: solid 1px #09F; border-radius:10px; background:#EFEFEF'>
    	<div style='background:#09F; margin-top:-10px;'><p style='font-size:2em; padding:5px;text-align:center; color:#FFF'>Chào mừng bạn đến với \"Find Your Friend\"</p></div>
        <div style='padding:0 20px'>
        	<p>Chào bạn,</p>
            <p>Chúng tôi đã được yêu cầu thay đổi mật khẩu từ email $_email. Nếu bạn không chắc đã yêu cầu thì vui lòng bỏ qua email này!</p>
            <p><i>Để đổi mật khẩu, bạn hãy vui lòng xác nhận bên dưới!</i></p>
            <a style='display: inline-block;
  height: 50px;  line-height: 50px;  padding-right: 30px;  padding-left: 30px;  position: relative;  background-color:#09F;  color:rgb(255,255,255);  text-decoration: none;  text-transform: uppercase;  letter-spacing: 1px;  margin-bottom: 15px;  border-radius: 5px;  -moz-border-radius: 5px;  -webkit-border-radius: 5px;  text-shadow:0px 1px 0px rgba(0,0,0,0.5);' href='"."http://www.sgulab.com/apps/findyourfriend/forgotpassword/changepassword.php?id=".$_id."&email=".$_email."&code=".$_code."'>Thay đổi mật khẩu</a>
        </div>
    </div>
</body>";
	
	return Send_Mail($to, $subject, $body);
}
?>