<?php
	
$servername = "localhost";
$username = "dame";
$password = "dame";
$dbname="lokaciiapp";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 
echo "Connected successfully1 <br>";

$ime=$_POST["name"];
$adresa=$_POST["address"];
$lat=$_POST["lat"];
$lng=$_POST["lng"];

//$ime="jiji";
//$adresa="nene";
//$lat="dada";
//$lng="dada"; 

$sql = "INSERT INTO lokaciitabela (lokacii_ime, lokacii_adresa, lokacii_lat,lokacii_lng) VALUES ('".$ime."','".$adresa."','".$lat."','".$lng."') ";


if (mysqli_multi_query($conn, $sql)) {
    echo "New records created successfully";
} else {
    echo "Error: " . $sql . "<br>" . mysqli_error($conn);
}



$conn->close();


?>

