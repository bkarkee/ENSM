<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="/css/userProfile.css">
<title>ENSM Update</title>
<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.png">
<%session.invalidate();%>

</head>
<body>

<div align="center">

	<div style="margin-top: 20%"></div>

	<form id= frm_Result>
		<h1><font color="Red" style="text-shadow: 2px 2px 2px #000000;">Password updated successfully .</font>
		</h1>
		<h1><font color="white">Please log in to your account</font></h1>
		<a href="/login" style="color: #53acef" >Go Back To Home Page</a>
	</form>
	
	<div style="margin-top: 20%"></div>
	
</div>

	<script type="text/javascript">
		setTimeout(function() {
			window.location = '/login';
		}, 5000);
	</script>
</body>
</html>