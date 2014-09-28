<?php
	function sendVerify($_id, $_email, $_hash){
		$to      = $_email; // Send email to our user
		$subject = 'SGU Lab - Find Your Friend : Sign Up | Verification'; // Give the email a subject 
		$message = '
		 
		Chào bạn!
		Thông tin của bạn đã được tạo, tuy nhiên vì  lý do bảo mật nên bạn cần xác nhận trước khi sử dụng tài khoản.
		 
		Vui lòng chọn liên kết bên dưới để kích hoạt tài khoản:
		http://sgulab.orgfree.com/activation/verify.php?id='.$_id.'&email='.$_email.'&hash='.$_hash.'
		 
		'; // Our message above including the link
							 
		//$headers = 'From:noreply@sgulab.orgfree.com' . "\r\n"; // Set from headers
		$headers = 'From:isgulab@gmail.com' . "\r\n"; // Set from headers
		echo mail($to, $subject, $message, $headers); // Send our email
	}
?>