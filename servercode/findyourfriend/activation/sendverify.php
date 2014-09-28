<?php
function sendVerify($_id, $_email, $_code){
	$rs=0;
	include 'smtp/Send_Mail.php';
	$to=$_email;
	$subject="FindYourFriend - Xác nhận đăng ký";
	$body="<body style='font-family:arial'>
	<div style='width:680px; height:auto; border: solid 1px #09F; border-radius:10px; background:#EFEFEF'>
    	<div style='background:#09F; margin-top:-10px;'><p style='font-size:2em; padding:5px;text-align:center; color:#FFF'>Chào mừng bạn đến với \"Find Your Friend\"</p></div>
        <div style='padding:0 20px'>
        	<p>Chào bạn,</p>
            <p>Lời đầu tiên xin cảm ơn các bạn đã dùng thử ứng dụng của nhóm!</p>
            <p>Find Your Friend là một ứng dụng tìm kiếm bạn bè. Ứng dụng được viết nhằm mục đích giúp mọi người dễ dàng tìm kiếm, liên lạc với những người bạn mới gần nơi mình sống, cung cấp nhiều tính năng tìm kiếm, đồng thời cho phép bạn dễ dàng liên lạc với bạn bè khi cần thiết thông qua tính năng nhắn tin miễn phí trực tuyến.</p>
            <p>Hy vọng ứng dụng này thực sự hữu ích với bạn!</p>
            <p>Chúc bạn luôn vui vẻ và thành công!</p>
            <p><i>Bạn hãy vui lòng xác nhận bên dưới để hoàn tất việc đăng ký!</i></p>
            <a style='display: inline-block;
  height: 50px;  line-height: 50px;  padding-right: 30px;  padding-left: 30px;  position: relative;  background-color:#09F;  color:rgb(255,255,255);  text-decoration: none;  text-transform: uppercase;  letter-spacing: 1px;  margin-bottom: 15px;  border-radius: 5px;  -moz-border-radius: 5px;  -webkit-border-radius: 5px;  text-shadow:0px 1px 0px rgba(0,0,0,0.5);' href='"."http://www.sgulab.com/apps/findyourfriend/activation/activation.php?id=".$_id."&email=".$_email."&code=".$_code."'>Xác nhận đăng ký</a>
        </div>
    </div>
</body>";
	
	return Send_Mail($to, $subject, $body);
}
?>