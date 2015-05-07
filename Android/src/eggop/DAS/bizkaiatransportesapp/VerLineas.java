package eggop.DAS.bizkaiatransportesapp;

import eggop.DAS.bizkaiatransportesapp.ListaLineasFragment.ListenerdeListaLineas;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.Menu;
import android.view.Surface;
import android.view.WindowManager;

public class VerLineas extends FragmentActivity implements ListenerdeListaLineas{

	public static VerLineas vl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ver_lineas);
		vl = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ver_lineas, menu);
		return true;
	}
	

	/**
	 * metodo resultante de la interfaz del Fragment que tiene la activity que permite la 
	 * comunicacion entre los dos. Esta interfaz contiene un unico fragment en caso de que
	 * el dispositivo este en retrato y contiene una vista lista-detalle en caso de que este
	 * en apaisado.
	 */
	@Override
	public void onItemSeleccionado(String item) {
		WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display mDisplay = mWindowManager.getDefaultDisplay();
		if(mDisplay.getRotation() == Surface.ROTATION_0 || mDisplay.getRotation() == Surface.ROTATION_180) {
			//rotation 0 y 180 = retrato
			Intent i = new Intent(this, DetallesLineaActivity.class);
			i.putExtra("contenido", item);
			startActivity(i);
		}else{
			//else = apaisado
			DetallesLínea f= (DetallesLínea) getSupportFragmentManager().findFragmentById(R.id.fragmentVerLineasDetalles);
			f.actualizarContenido(item);
		}
	}

}
