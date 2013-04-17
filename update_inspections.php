<?php 
	include("inspection_class.php");
	include("include.php");

	$con = mysql_connect($mysql_host,$mysql_user,$mysql_password);
	mysql_select_db($mysql_database,$con);

	$datetime=date('ymdHis'); 

	$xmlfile = "myfile" . $datetime . ".xml";
	$FileHandle = fopen($xmlfile,'w');
	fwrite($FileHandle, stripslashes($_POST['m-inspect']));
	fclose($FileHandle);

	$xml = new DOMDocument();
	$xml->load($xmlfile);
	
	$returnXML = '<?xml version="1.0" encoding="UTF-8" ?><inspections>';
	
	$inspection = $xml->getElementsByTagName("inspection");
	foreach ($inspection AS $item)
  	{
  		$hold = $item->getElementsByTagName("policy");
  		$policy = $hold->item(0)->nodeValue;
  		$hold = $item->getElementsByTagName("lat");
  		$lat = $hold->item(0)->nodeValue;
  		$hold = $item->getElementsByTagName("long");
  		$long = $hold->item(0)->nodeValue;
	
		$query = "UPDATE inspections SET latitude='$lat',longitude='$long' WHERE policy='$policy'";
		if(mysql_query($query)){
			$returnXML .= "<inspection><policy>$policy</policy><updated>true</updated></inspection>";
		}
  	}

  	$returnXML .= "</inspections>";

  	echo $returnXML;

  	mysql_close($con);
  	unlink($xmlfile);
?> 

