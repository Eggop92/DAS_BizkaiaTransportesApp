package eggop.DAS.bizkaiatransportesapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class ServicioGCM extends IntentService {

	public ServicioGCM() {
		super("ServicioGCM");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		
		String messageType = gcm.getMessageType(intent);
		Bundle extras = intent.getExtras();
		
		if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
			if(!extras.isEmpty()){
				String linea = intent.getExtras().getString("linea");
				if(!linea.isEmpty()){
					Intent i = new Intent(this, DetallesLineaActivity.class);
					//Intent i = new Intent(this, MainMenu.class);
					i.putExtra("contenido", linea);
					PendingIntent intentParaLaNotificacion = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT);
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
					mBuilder.setSmallIcon(R.drawable.ic_launcher)
							.setAutoCancel(true)
							.setLargeIcon((((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap()))
							.setContentTitle("Linea "+linea+" actualizada")
							.setContentText("Se ha realizado una actualización de una linea favorita")
							.setTicker("Linea "+linea+" actualizada")
							.setContentIntent(intentParaLaNotificacion);
					NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					mNotificationManager.notify(0, mBuilder.build());
				}
			}
		}
		RecibidorGCM.completeWakefulIntent(intent);
	}



}
