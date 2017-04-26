<?php

// API access key from Google API's Console
define( 'API_ACCESS_KEY', 'AAAAKF9MsBM:APA91bH-d4dJDCRNtVO0Qeec2JhoPVihaLkygbCPxY4hzDmzaURDO1H29cvWKr0gSskt7708W8coviqgkpAHiYBZqijkjMbZ2j7KJovwCunGg3jzRP5NlF8QzlJKMgJ4MjHd_spA_C6n' );

$registrationIds = array();

$mysqli = new mysqli("localhost", "nilanb", "nilanb123~", "gcm");

if($mysqli->connect_errno){

  echo "Could not connect to the database\n";
  echo "Error: ". $mysqli->connect_errno . "\n";
  exit();
}


$query = "SELECT * from tokens";

if ($result = $mysqli->query($query)) {

    /* fetch associative array */
    while ($row = $result->fetch_assoc()) {
        $registrationIds[] = $row['token'];
    }

    /* free result set */
    $result->free();
}

foreach($registrationIds as $value) 
	print($value . "\n");

$mysqli->close();

// prep the bundle
$msg = array
(
	'message' 	=> 'Here is a message. message',
	'title'		=> 'This is a title. title',
	'subtitle'	=> 'This is a subtitle. subtitle'
);
$fields = array
(
	'registration_ids' 	=> $registrationIds,
	'data'			=> $msg
);
 
$headers = array
(
	'Authorization: key=' . API_ACCESS_KEY,
	'Content-Type: application/json'
);
 
$ch = curl_init();
curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
curl_setopt( $ch,CURLOPT_POST, true );
curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
$result = curl_exec($ch );
echo $result;
curl_close( $ch );

?>
