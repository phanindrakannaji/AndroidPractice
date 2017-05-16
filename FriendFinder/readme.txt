Friend Finder App Documentation: (more like a Friend Locator :P)

Video: https://drive.google.com/open?id=0BxP9HqhPaqpFWVVNVFpnNHhjdTQ

The server is deployed in AWS and the SSH Key to connect to the EC2 instance is provided in the zip file. 

Command: 

ssh -i <PATH_TO_KEY>/friendfinder.pem ec2-user@52.201.42.250

After connecting the PHP scripts are placed at /var/www/html and also in WS directory of the zip file.

Database:

Within the server, execute:

mysql -h friendfinder.cav3dmgwgclk.us-east-1.rds.amazonaws.com -P 3306 -u pkanna1 -p

Password: CMSC6282

Database schema: (Only one table)

	email VARCHAR(20) PRIMARY KEY,
	fullName VARCHAR(20),
	password VARCHAR(20),
	latestTimestamp DATETIME,
	lastAlertedTime DATETIME,
	latitude DECIMAL(16, 10),
	longitude DECIMAL(16,10),
	token varchar(1024)

Assumptions:

The friend will be shown in the map only when the last active time is greater than one hour and when the distance is less than 1 km.

Firebase Authentication and Firebase Cloud messaging is also implemented as an extra and will not collide with the required functionality.