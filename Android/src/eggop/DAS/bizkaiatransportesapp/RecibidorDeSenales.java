package eggop.DAS.bizkaiatransportesapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RecibidorDeSenales extends BroadcastReceiver {

	/**
	 * Clase que recibe las señales del sistema, 
	 * concretamente para que se lance el servicio
	 * al reiniciar el dispositivo.
	 */
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent i = new Intent(context, ServicioDetectarProximidad.class);
		context.startService(i);
	}

}
