<?php

#Conexión a la BD
$DB_SERVER="localhost"; #la dirección del servidor
$DB_USER=""; #el usuario de MySQL para esa base de datos
$DB_PASS=""; #la clave para ese usuario
$DB_DATABASE="BizkaiaTransportesApp"; #la base de datos a la que hay que conectarse

$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

#Comprobar conexión
if (mysqli_connect_errno()) { 
	echo 'Error de conexion: ' . mysqli_connect_error();
	exit();
}

function cerrarConexion($con){
	mysqli_close($con);
}

?>