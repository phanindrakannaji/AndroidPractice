
<?php

include "dbinfo.inc";

$data = json_decode(file_get_contents('php://input'), true);

$latitude = floatval(0);
$longitude = floatval(0);
$username = $data["username"];
$password = $data["password"];
$fullName = $data["fullName"];

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
$res = $mysqli->query("SELECT username FROM ".$table." WHERE LOWER(username) = LOWER('". $username ."')");
if (!$res || $res->num_rows == 0 ){
	# Insert entry for user if user doesn't exist
	$query = "INSERT INTO $table(username, fullName, password, latestTimestamp, latitude, longitude, token) VALUES(LOWER('$username'), '$fullName', '$password', now(), $latitude, $longitude, '')";
	$result = $mysqli->query($query);
	if(!$result)
	{
		$error = "Register: Data was not inserted into the table " . $mysqli->error . "\n";
		$errorOccurred = true;
	}
} else{
	$error = "Register: Username already exists!!! \n";
	$errorOccurred = true;
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
