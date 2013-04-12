<?php 
	include("inspection_class.php");
	include("include.php");

	$con = mysql_connect($mysql_host,$mysql_user,$mysql_password);
	mysql_select_db($mysql_database,$con);
	$query = "SELECT * FROM inspections WHERE latitude IS NULL AND longitude IS NULL ORDER BY policy";
	$result = mysql_query($query);
	$xml = '<?xml version="1.0" encoding="UTF-8" ?><inspections>';
	while($row = mysql_fetch_array($result))
	{
		$xml .= "<inspection>";
		$xml .= "<policy>$row[policy]</policy>";
		$xml .= "<name>$row[name]</name>";
		$xml .= "<name2>$row[name2]</name2>";	
		$xml .= "<address>$row[address]</address>";
		$xml .= "<address2>$row[address2]</address2>";
		$xml .= "<cityst>$row[cityst]</cityst>";
		$xml .= "</inspection>";			
	}
	$xml .= "</inspections>";
	
	echo $xml;
?>