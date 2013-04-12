<?php 
	include("inspection_class.php");
	include("include.php");

	$xml = simplexml_load_file("http://jimcloudy.comze.com/inspect/test_xml.php");

	echo $xml->getName() . "<br>";

	foreach($xml->children() as $child)
  	{
  		echo $child->getName() . ": " . $child->name . "<br>";
  	}

?> 

