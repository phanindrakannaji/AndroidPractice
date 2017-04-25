
<?php

include "dbinfo.inc";

$data = json_decode(file_get_contents('php://input'), true);
$username = $data["username"];

$table = "friends";

#Connect to MySql server
$mysqli = new mysqli(DB_SERVER, DB_USERNAME, DB_PASSWORD, DB_DATABASE);

if ($mysqli->connect_errno) {
    die("[ERROR] Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error);
}

$res = $mysqli->query("SELECT username, firstName, lastName, latestTimestamp, latitude, longitude  FROM " .$table. " WHERE username <> '$username' ORDER BY username ASC");
$jsonMainArr = array();
while ($row = $res->fetch_assoc()) {
    $jsonArr["username"] = $row['username'];
    $jsonArr["firstName"] = $row['firstName'];
    $jsonArr["lastName"] = $row['lastName'];
    $jsonArr["latestTimestamp"] = $row['latestTimestamp'];
    $jsonArr["latitude"] = $row['latitude'];
    $jsonArr["longitude"] = $row['longitude'];
    $jsonMainArr[] = $jsonArr;
}

header('Content-type: application/json');
echo json_encode($jsonMainArr);

$mysqli->close();


?>
