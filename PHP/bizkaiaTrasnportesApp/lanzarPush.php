<?php

if (isset($_GET['Linea'])){
	$linea = $_GET['Linea'];
	require_once __DIR__ . '/conexion.php';
	$cabecera = array(
			'Authorization:key=AIzaSyBQ-86aBpSfzb5g8dd-hlie_0xGVZwWIyA',
			'Content-Type:application/json'
		);
	//rellenar los ids de la bd
	//$ids = array('APA91bGJFYnsQgPBQKwS0hmqzwCf4rx7FvIAwiZ7LQCCWz057qSQHho-y0Q8wrWm2NpOezwzCpDxo9ANuQaxcQR1Qm6pTbt1JtGRA-3cJRwRMCGrvApkG8JN6D5o-Kflyw2I78R6LKAE4q8CLaWN8K5D725_FFjqioQ0JqSddZ_-IME8oxRfLm3XxQmbyVf6RzRyyyEnEnqa');
	$ids = array();
	$resultado = mysqli_query($con, "SELECT idGCM FROM usuarios NATURAL JOIN favoritos WHERE linea ='".$linea."';");
	if(!$resultado){
    	echo "Error (3) mysql: ".mysqli_error($con);
    }else{
    	while($fila = mysqli_fetch_row($resultado)){
    		array_push($ids, $fila[0]);
    	}
    }

	//rellenar la info que se quiere enviar
	$data = array('linea' => $linea);
	$info = array(
			'registration_ids' => $ids,
			'collapse_key' => 'Actualizacion',
			'time_to_live' => 200,
			'delay_while_idle' => true,
			'data' => $data
		);
	echo json_encode($info)."<br><br>";
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, 'https://android.googleapis.com/gcm/send');
	curl_setopt($ch, CURLOPT_POST, true);
	curl_setopt($ch, CURLOPT_HTTPHEADER, $cabecera);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($info));
	$result = curl_exec($ch);
	curl_close($ch);
	echo $result;
}else{
	echo "Introduce por parametros la linea que quieras actualizar.";
}


?>