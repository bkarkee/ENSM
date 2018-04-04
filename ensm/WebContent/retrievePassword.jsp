<!DOCTYPE html>
<html>
<head>

	<title>ENSM Retrieve Password</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" type="text/css" href="/css/index.css">
	<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.png">

</head>

<body>
	<br>
	
	<h1 style="color: #ebebe0;">Emergency Notification System</h1>

	<h2 style="color: #ebebe0;">
		Password Reset <br><br>
		<img style="align: center" src="/images/logoibm.jpg">
	</h2>

	<div align="center">
		
		<form action="Servlet"  method="post" accept-charset=utf-8>
			<input type="hidden" name="requestType"  value="sendMail">
			<h3>Enter your IBM email to change your password</h3>
			
			<p>Email: &nbsp;<input maxlength=50 autocomplete=off required style="position: relative; background-color:#060c0f;" type="text"  name="email" autofocus/></p>

			<br> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
			<button type = "submit" class="buttonopt">Submit</button>

		</form>
	</div>
</body>
</html>




 