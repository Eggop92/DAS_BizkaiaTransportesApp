<?php

if (isset($_POST['GCM_ID']) && isset($_POST['registradoGCM'])) {
	require_once __DIR__ . '/conexion.php';
	$usuario = $_POST['registradoGCM'];
	$idGCM = $_POST['GCM_ID'];
	$resultado = mysqli_query($con, "INSERT INTO usuarios (usuario, idGCM) VALUES ('".$usuario."', '".$idGCM."');");
	if(!$resultado){
		$response = array();
		// required field is missing
    	$response["ERROR"] = "Error (8) mysql: ".mysqli_error($con);
   		// echoing JSON response
    	echo json_encode($response);
    }else{
    	$response = array();
		// required field is missing
    	$response["OK"] = "Registro correcto.";
   		// echoing JSON response
    	echo json_encode($response);
    }
    cerrarConexion($con);
}else{
	$response = array();
	// required field is missing
	$response["ERROR"] = "Faltan parametros necesarios.";
	// echoing JSON response
	echo json_encode($response);
}



?>