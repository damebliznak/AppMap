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
$sql = "SELECT * FROM lokaciitabela";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        echo "id:". $row["lokacii_id"]."	ime:".$row["lokacii_ime"]."<br>";
    }
} else {
    echo "0 results";
}

if (mysqli_multi_query($conn, $sql)) {
    echo "New records created successfully";
} else {
    echo "Error: " . $sql . "<br>" . mysqli_error($conn);
}



$conn->close();






?>

