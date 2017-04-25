
<?php

include "dbinfo.inc";

$data = json_decode(file_get_contents('php://input'), true);

$latitude = floatval(0);
$longitude = floatval(0);
$username = $data["username"];
$password = $data["password"];

$table = "friends";

#Connect to MySql server
$mysqli = new mysqli(DB_SERVER, DB_USERNAME, DB_PASSWORD, DB_DATABASE);

if ($mysqli->connect_errno) {
    die("[ERROR] Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error);
}
$username = $mysqli->real_escape_string($username);
$password = $mysqli->real_escape_string($password);
$error = "";
$errorOccurred = false;

# Check if user exists
$res = $mysqli->query("SELECT username FROM ".$table." WHERE LOWER(username) = LOWER('$username')");
if (!$res || $res->num_rows == 0 ){
	$error = "Login: Username does not exist!!! \n";
	$errorOccurred = true;
} else{
	$res = $mysqli->query("SELECT username FROM ".$table." WHERE LOWER(username) = LOWER('$username') and password = '$password'");
	if (!$res || $res->num_rows == 0 ){
		$error = "Login: No matching account for username and password!! \n";
		$errorOccurred = true;
	}
}

$jsonMainArr = array();
if ($errorOccurred){
	$jsonArr["status"] = "F";
	$jsonArr["errorMessage"] = $error;
	$jsonMainArr[] = $jsonArr;
} else{
	$res = $mysqli->query("SELECT username, fullName, latestTimestamp, latitude, longitude  FROM " .$table. " WHERE LOWER(username) = LOWER('$username') ORDER BY username ASC");
	while ($row = $res->fetch_assoc()) {
		$jsonArr["status"] = "S";
	    $jsonArr["username"] = $row['username'];
	    $jsonArr["fullName"] = $row['fullName'];
	    $jsonArr["latestTimestamp"] = $row['latestTimestamp'];
	    $jsonArr["latitude"] = $row['latitude'];
	    $jsonArr["longitude"] = $row['longitude'];
	    $jsonMainArr[] = $jsonArr;
	}
}

header('Content-type: application/json');
echo json_encode($jsonMainArr);

$mysqli->close();


?>
