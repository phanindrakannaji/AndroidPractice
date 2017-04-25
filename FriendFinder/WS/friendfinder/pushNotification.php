
<?php

include "dbinfo.inc";

define('API_ACCESS_KEY', 'AAAA-i04Bis:APA91bFGeCi7umkTQveON03UFznMMq78SV_HjqkXUhf3p165c1g6JwmwuM_CY2jSGDjwBOkjishvorlAxvx0ZSW5MqI_Lg02834cpq9CLwQM_2cI_W8YwLZYOTDzq3vpF8SKt6l6fJ6K');
$table = "friends";

$registrationids = array();
#Connect to MySql server
$mysqli = new mysqli(DB_SERVER, DB_USERNAME, DB_PASSWORD, DB_DATABASE);

if ($mysqli->connect_errno) {
    die("[ERROR] Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error);
}

$registrationids = array();

$res = $mysqli->query("SELECT username, token  FROM " .$table. " ORDER BY username ASC");
while ($row = $res->fetch_assoc()) {
    $username = $row['username'];
    $registrationids[] = $row['token'];
}

$msg = array(
    	'message' => 'Hello World message',
    	'title' => 'FCM Message',
    	'subtitle' => 'Subtitle'
    	);

 $fields = array(
    	'registration_ids' => $registrationids,
    	'data' => $msg
    	);

$headers = array(
	"Authorization: key=" . API_ACCESS_KEY,
	'Content-Type: application/json'
	);

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, "https://fcm.google.comapis.com/fcm/send");
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

$result = curl_exec($ch);
echo $result;
curl_close($ch);


$mysqli->close();

?>
