<?php
	
$servername = "localhost";
$username = "dame";
$password = "dame";

// Create connection
$conn = new mysqli($servername,$username,$password);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 
echo "Connected successfully1";



?>

