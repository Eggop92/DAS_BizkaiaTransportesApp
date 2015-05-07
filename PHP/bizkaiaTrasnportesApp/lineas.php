<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

function imprimirError($error){
	$response = array();
	// required field is missing
    $response["ERROR"] = $error;
    // echoing JSON response
    echo json_encode($response);
    //exit;
}

function listarLineas(){
	// include db connect class
    require_once __DIR__ . '/conexion.php';

    //sentencia sQL
    $resultado = mysqli_query($con, "SELECT tipo, numero, descripcion, frecuencia FROM Lineas;");
    //comprobacion de error
    if(!$resultado){
    	imprimirError("Error (1) mysql: ".mysqli_error($con));
    }else{
    	$response = array();
    	$response['Lineas'] = array();
    	while($fila = mysqli_fetch_row($resultado)){
    		$array = array();
    		$array['tipo'] = $fila[0];
    		$array['numero'] = $fila[1];
    		$array['descripcion'] = $fila[2];
    		$array['frecuencia'] = $fila[3];
    		array_push($response['Lineas'], $array);
    	}
    	cerrarConexion($con);
    	echo json_encode($response);
    }
}

function infoLinea($linea){
	// include db connect class
    require_once __DIR__ . '/conexion.php';

	//sentencia sQL
    $resultado = mysqli_query($con, "SELECT tipo, numero, descripcion, frecuencia FROM Lineas WHERE numero='".$linea."';");   
     //comprobacion de error
    if(!$resultado){
    	imprimirError("Error (2) mysql: ".mysqli_error($con));
    }else{
    	
    	if($fila = mysqli_fetch_row($resultado)){
    		$objeto['tipo'] = $fila[0];
    		$objeto['numero'] = $fila[1];
    		$objeto['descripcion'] = $fila[2];
    		$objeto['frecuencia'] = $fila[3];
    	}
    	$response['Linea'] = $objeto;
    	cerrarConexion($con);
    	echo json_encode($response);
    }
}

function ParadasLinea($linea){
	// include db connect class
    require_once __DIR__ . '/conexion.php';

    //sentencia sQL
    $resultado = mysqli_query($con, "SELECT id, direccion FROM paradas INNER JOIN recorridos ON id=Pid WHERE Lnum='".$linea."' ORDER BY numParLinea;");
    //comprobacion de error
    if(!$resultado){
    	imprimirError("Error (3) mysql: ".mysqli_error($con));
    }else{
    	$response['Paradas'] = array();
    	while($fila = mysqli_fetch_row($resultado)){
    		$array = array();
    		$array['id'] = $fila[0];
    		$array['direccion'] = $fila[1];
    		array_push($response['Paradas'], $array);
    	}
    	cerrarConexion($con);
    	echo json_encode($response);
    }
}

function MarcarLinea($linea, $usuario, $favorito){
	// include db connect class
    require_once __DIR__ . '/conexion.php';
    if($favorito == "true"){
    	//incluimos en la base de datos y mandamos las sentencias SQL a ejecutar en el cliente
    	//sentencia sQL
    	//echo "usuario =".$usuario;
    	$resultado = mysqli_query($con, "INSERT INTO favoritos (usuario, linea) VALUES ('".$usuario."', '".$linea."');");
    	if(!$resultado){
    		imprimirError("Error (8) mysql: ".mysqli_error($con));
    	}else{
	    	$response = array();
	    	$resultado = mysqli_query($con, "SELECT tipo, numero, Descripcion, frecuencia FROM Lineas WHERE numero='".$linea."';");
	    	if(!$resultado){
	    		imprimirError("Error (4) mysql: ".mysqli_error($con));
	    	}else{
	    		if($fila = mysqli_fetch_row($resultado)){
	    			$descLinea = array();
		    		$descLinea['tipo'] = $fila[0];
		    		$descLinea['numero'] = $fila[1];
		    		$descLinea['descripcion'] = $fila[2];
		    		$descLinea['frecuencia'] = $fila[3];
		    		$response['Linea']= $descLinea;
		    		$resultado = mysqli_query($con, "SELECT id, direccion, lat, lon FROM paradas INNER JOIN recorridos ON id=Pid WHERE Lnum='".$linea."' ORDER BY numParLinea;");
		    		if(!$resultado){
	    				imprimirError("Error (6) mysql: ".mysqli_error($con));
	    			}else{
	    				$response['Paradas'] = array();
	    				while($fila = mysqli_fetch_row($resultado)){
	    					$parada = array();
	    					$parada['id'] = $fila[0];
	    					$parada['direccion'] = $fila[1];
	    					$parada['lat'] = $fila[2];
	    					$parada['lon'] = $fila[3];
	    					array_push($response['Paradas'], $parada);
	    				}
	    				$resultado = mysqli_query($con, "SELECT Lnum, Pid, numParLinea, tiempo FROM recorridos where Lnum='".$linea."';");
	    				if(!$resultado){
	    					imprimirError("Error (7) mysql: ".mysqli_error($con));
	    				}else{
	    					$response['Recorridos'] = array();
	    					while($fila = mysqli_fetch_row($resultado)){
	    						$recorrido = array();
	    						$recorrido['Lnum'] = $fila[0];
	    						$recorrido['Pid'] = $fila[1];
	    						$recorrido['numParLinea'] = $fila[2];
	    						$reccorido['tiempo'] = $fila[3];
	    						array_push($response['Recorridos'], $recorrido);
	    					}
	    					echo json_encode($response);
	    				}
	    			}
	    		}else{
	    			imprimirError("Error (5) mysql: ".mysqli_error($con));
	    		}
	    	}
    	}
    }else{
    	//en caso de que el usuario haya desmarcado el favorito
    	$resultado = mysqli_query($con, "DELETE FROM favoritos WHERE usuario='".$usuario."' and linea='".$linea."';");
	    if(!$resultado){
	    	imprimirError("Error (9) mysql: ".mysqli_error($con));
	    }else{
	    	$response = array();
    		$response["INFO"] = "Eliminacion correcta";
    		echo json_encode($response);
    	}
    }
	

}

function PosParada($parada){
	require_once __DIR__ . '/conexion.php';
	$resultado = mysqli_query($con, "SELECT lat, lon FROM paradas WHERE id='".$parada."';");
	if(!$resultado){
		imprimirError("Error (10) mysql: ".mysqli_error($con));
	}else{
		$response = array();
		if($fila = mysqli_fetch_row($resultado)){
			$response['lat'] = $fila[0];
			$response['lon'] = $fila[1];
		}
		cerrarConexion($con);
    	echo json_encode($response);
	}
}

//5.39.80.193/DAS/BizkaiaTransportesApp/lineas.php
//comprobamos si el parametro existe para emitir un error en caso de que no sea así
if (isset($_POST['FUNCION']) ) {
	$funcion = $_POST['FUNCION'];

    //separamos por funciones
    if($funcion == 'selLineas'){

    	listarLineas();

    }elseif($funcion == 'infoLineas'){

    	//nos aseguramos de que estan todos los parametros necesarios, o sino emitimos error
    	if(isset($_POST['LINEA'])){
    		infoLinea($_POST['LINEA']);
    	}else{
    		imprimirError("No se ha definido un parametro necesario para la funcion infoLineas");
    	}

    }elseif($funcion == 'ParadasLineas'){

    	//nos aseguramos de que estan todos los parametros necesarios, o sino emitimos error
    	if(isset($_POST['LINEA'])){
    		ParadasLinea($_POST['LINEA']);
    	}else{
    		imprimirError("No se ha definido un parametro necesario para la funcion ParadasLineas");
    	}

    }elseif($funcion == 'MarcarLinea'){

    	//nos aseguramos de que estan todos los parametros necesarios, o sino emitimos error
    	if(isset($_POST['LINEA']) && isset($_POST['USUARIO']) && isset($_POST['FAVORITO'])){
    		MarcarLinea($_POST['LINEA'], $_POST['USUARIO'], $_POST['FAVORITO']);
    	}else{
    		imprimirError("No se ha definido un parametro necesario para la funcion MarcarLinea");
    	}
    }elseif($funcion == 'PosParada'){
    	//nos aseguramos de que estan todos los parametros necesarios, o sino emitimos error
    	if(isset($_POST['ID_PARADA'])){
    		PosParada($_POST['ID_PARADA']);
    	}else{
    		imprimirError("No se ha definido un parametro necesario para la funcion PosParada");
    	}
    }else{
    	//imprimimos un error si la funcion que se busca no esta definida
    	imprimirError("La funcion no esta definida");
    }

}else{
	imprimirError("No se ha definido la variable funcion");
}




?>