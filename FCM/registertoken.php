<?php
$token = $_REQUEST['token'];
$mysqli = new mysqli("localhost", "nilanb", "nilanb123~", "fcm");
if($mysqli->connect_errno){
	print("Error connecting to the database: " . $mysqli->connect_errno ."\n");
	exit();
}
$query = "INSERT into tokens(token) values('$token')";
$result = $mysqli->query($query);
if(!$result)
{
	print("Data not inserted" . $mysqli->error . "\n");
} 
$mysqli->close();
?>

