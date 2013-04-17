<?php 
	include("inspection_class.php");
	include("include.php");

	$con = mysql_connect($mysql_host,$mysql_user,$mysql_password);
	mysql_select_db($mysql_database,$con);
	$query = "SELECT * FROM inspections WHERE latitude IS NULL AND longitude IS NULL ORDER BY policy IFNULL(field, " ")";
	$result = mysql_query($query);
	$inspections = array();
	while($row = mysql_fetch_assoc($result))
	{
		$inspections[] = $row;
	}

	mysql_close($con);
	header('Content-type: application/json');
	echo json_encode(array('inspections'=>$inspections));
?>
