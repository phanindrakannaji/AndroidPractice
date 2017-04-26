
<?php

include "dbinfo.inc";

$table = "friends";
$mysqli = new mysqli(DB_SERVER, DB_USERNAME, DB_PASSWORD, DB_DATABASE);

if ($mysqli->connect_errno) {
    die("[ERROR] Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error);
}

if (!$mysqli->query("DROP TABLE IF EXISTS " . $table)) {
    die("[ERROR] Failed Dropping the table " . $table . " failed: (" . $mysqli->errno . ") " . $mysqli->error);
}

if (!$mysqli->query("CREATE TABLE " . $table . "(
	email VARCHAR(20) PRIMARY KEY,
	fullName VARCHAR(20),
	password VARCHAR(20),
	latestTimestamp DATETIME,
	latitude DECIMAL(16, 10),
	longitude DECIMAL(16,10),
	token varchar(1024)
	)")) {
    die("[ERROR] Failed to create table " . $table . " : (" . $mysqli->errno . ") " . $mysqli->error);
}

$mysqli->close();

?>
