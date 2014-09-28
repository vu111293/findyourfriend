<?php
function Send_Mail($to,$subject,$body)
{
	require 'class.phpmailer.php';
	$from       = "isgulab@gmail.com";
	$mail       = new PHPMailer();
	$mail->CharSet="UTF-8";
	$mail->IsSMTP(true);            // use SMTP
	$mail->IsHTML(true);
	$mail->SMTPAuth   = true;                  // enable SMTP authentication
	$mail->SMTPSecure = "ssl";
	$mail->SMTPDebug  = 1;
	$mail->Host       = "smtp.gmail.com"; // Amazon SES server, note "tls://" protocol
	$mail->Port       =  465;                    // set the SMTP port
	$mail->Username   = "isgulab@gmail.com";  // SMTP  username
	$mail->Password   = "accentlupin1";  // SMTP password
	$mail->SetFrom($from);//$mail->SetFrom($from, 'isgulab');
	$mail->AddReplyTo($from);
	$mail->Subject    = $subject;
	$mail->MsgHTML($body);
	$address = $to;
	$mail->AddAddress($address, $to);
	$mail->Send();
	return $mail->ErrorInfo;
}
?>
