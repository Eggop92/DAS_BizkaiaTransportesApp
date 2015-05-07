package eggop.DAS.bizkaiatransportesapp;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class ServicioDetectarProximidad extends Service {

	/**
	 * Clase que ejerce de servicio.
	 */
	
	Timer mTimer;
	
	/**
	 * El servicio crea un TimerTask que se programa para lanzar la busqueda cada 15 min
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(getApplicationContext(), "El servicio esta en ejecucion", Toast.LENGTH_SHORT).show();
		this.mTimer = new Timer();
		this.mTimer.scheduleAtFixedRate(
				new TimerTask(){
					@Override
					public void run() {
						ejecutarTarea();
					}      
				}
				, 0, 1000 * 60 * 15);//Cada 15 min
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	
	
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);
		int rdo=START_REDELIVER_INTENT;
		ejecutar();
		
		return rdo;
	}

	private void ejecutarTarea(){
		Thread t = new Thread(new Runnable() {
			public void run() {
				//Qué se tiene que ejecutar
				ejecutar();
			}
		});  
		t.start();
	}


	/**
	 * Tarea a ejecutar por el servicio cada 15 minutos. Busca entre las lineas que
	 * el usuario ha marcado como favoritas, la parada que tiene mas cerca y si esta dentro 
	 * del radio definido. 
	 */
	private void ejecutar() {
		//Cargamos el servicio de localización y buscamos un servicio que este activo
		LocationManager elmanager= (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria loscriterios = new Criteria(); 
		loscriterios.setAccuracy(Criteria.ACCURACY_COARSE);
		String mejorproveedor= elmanager.getBestProvider(loscriterios, true);
		Location pos = elmanager.getLastKnownLocation(mejorproveedor);
		if(pos != null){
			//Si se dispone de una  posición, se busca en la base de datos la parada mas cercana
			//se abre la bd y se pide la parada.
			BaseDeDatos bd = new BaseDeDatos(getApplicationContext(), "bizkaiaTransportesApp", null, 1);
			String datos = bd.paradaMasCercanaAMinimaDistancia(pos.getLatitude(), pos.getLongitude());
			if(datos != null){
				//Si hay una parada cercana, se crea un intent para abrir la aplicacion y 
				//lanzar una notificacion en la barra de estado
				//creamos el intent
				Intent i = new Intent(this, DetallesLineaActivity.class);
				i.putExtra("contenido", datos.split(" - ")[1]);
				PendingIntent intentParaLaNotificacion = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
				//creamos la notificacion y le añadimos el intent
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
					.setSmallIcon(android.R.drawable.ic_dialog_map)
					.setAutoCancel(true)
					.setLargeIcon((((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap()))
					.setContentTitle(getApplication().getText(R.string.parada)+" "+datos.split(" - ")[0]+" "+getApplication().getText(R.string.cerca))
					.setContentText(getApplication().getText(R.string.linea)+" "+datos.split(" - ")[1])
					.setContentInfo(datos.split(" - ")[2]+" "+getApplication().getText(R.string.min))
					.setContentIntent(intentParaLaNotificacion)
					.setTicker(getApplication().getText(R.string.tituloNotific));
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(1, mBuilder.build());
			}
		}
	}
	
	public void onDestroy(){
		super.onDestroy();
	}

	
	
}
