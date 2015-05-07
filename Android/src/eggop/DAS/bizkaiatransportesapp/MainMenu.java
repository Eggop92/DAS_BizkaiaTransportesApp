package eggop.DAS.bizkaiatransportesapp;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainMenu extends Activity {
	
	
	private Context context;
	
	/**
	 * ACTIVIDAD PRINCIPAL
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!checkPlayServices()){
			finish();
		}
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean registrado = pref.getBoolean(RegistroGCM.REGISTRADO_GCM, false);
		if(!registrado){
			context = getApplicationContext();
			registrarse();
		}
		setContentView(R.layout.activity_main_menu);
		
	}
	
	protected void onResume(){
		super.onResume();
		if(!checkPlayServices()){
			finish();
		}
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean registrado = pref.getBoolean(RegistroGCM.REGISTRADO_GCM, false);
		if(!registrado){
			context = getApplicationContext();
			registrarse();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	/**
	 * Creamos el listener para detectar la pulsación sobre el menu para cambiar de idioma.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		//nos aseguramos que el item pulsado es el que corresponde al menu de idioma
		if(item.getItemId()==R.id.action_settings){
			//Generamos el dialog de monoselección y forzamos el idioma cuando selecciona el idioma.
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.seleciona_idioma);
			dialog.setSingleChoiceItems(R.array.Idiomas, 0, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String cidioma = getResources().getStringArray(R.array.CodigoIdiomas)[which];
					Locale nuevaloc = new Locale(cidioma); 
					Locale.setDefault(nuevaloc); 
					Configuration config = new Configuration(); 
					config.locale = nuevaloc; 
					getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
					setContentView(R.layout.activity_main_menu);
					dialog.cancel();
				}
			});
			dialog.show();
			return true;
		}
		return false;
	}
	
	/**
	 * listener para lanzar la activity de ver las lineas y sus paradas
	 * @param v
	 */
	public void botonVer(View v){
		Intent i = new Intent(this, VerLineas.class);
		startActivity(i);
	}
	
	/**
	 * listener para lanzar el servicio, realmente no deberia de verse, es comodidad para
	 * probar el servicio.
	 * @param v
	 */
	public void botonServicio(View v){
		Intent i = new Intent(getApplicationContext(), ServicioDetectarProximidad.class);
		startService(i);
	}
	

	private boolean checkPlayServices(){
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(resultCode != ConnectionResult.SUCCESS){
			if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
				//dispositivo no configurado
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, 9000).show();
			}else{
				//dispositivo no compatible
				Log.i("INFO", "This device is not supported.");
			}
			return false;
		}
		return true;
	}
	
	private void registrarse(){
		RegistroGCM registro = new RegistroGCM(context);
		registro.execute();
	}
	
}
