package eggop.DAS.bizkaiatransportesapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class DetallesLineaActivity extends FragmentActivity {
	/**
	 * Clase destinada a almacenar el fragment cuando el dispositivo
	 * esta en posici�n retrato.
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalles_linea);
		
		//recogida del fragment y de la informaci�n contenida en el intent
		DetallesL�nea f= (DetallesL�nea) getSupportFragmentManager().findFragmentById(R.id.fragmentDetallesLinea);
		String lainfo=getIntent().getStringExtra("contenido");
		//actualiza el fragment con la info del intent.
		f.actualizarContenido(lainfo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.detalles_linea, menu);
		return true;
	}

}
