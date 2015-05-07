package eggop.DAS.bizkaiatransportesapp;

import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.RemoteViews;


public class Widget extends AppWidgetProvider{
	

	@Override
	public void onEnabled(Context context) {
		// Se ejecuta al crear la primera instancia del widget
		super.onEnabled(context);
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// Se ejecuta al borrar una instancia del widget
		super.onDeleted(context, appWidgetIds);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// Se ejecuta cada vez que pasan los segundos definidos en el widget.xml
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.i("WIDGET", "onUpdate");
		for(int i=0; i<appWidgetIds.length; i++){
			actualizarWidget(context, appWidgetManager, appWidgetIds[i]);
		}
	}
	
	public void onReceive(Context context, Intent intent){
		AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
		if(intent.getAction().equals("eggop.DAS.bizkaiatransportesapp.WidgetConfiguracion")){
			Log.i("WIDGET", "Widget.onReceive.intent.WidgetConfiguracion");
			int widgetid = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			if(widgetid != AppWidgetManager.INVALID_APPWIDGET_ID){
				Log.i("WIDGET", "Widget.onReceive.id");
				actualizarWidget(context, widgetManager, widgetid);
			}
		}else if(intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")){
			Log.i("WIDGET", "Widget.onReceive.intent.APPWIDGET_UPDATE");
			int[] widgetids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
			if(widgetids != null){
				Log.i("WIDGET", "Widget.onReceive.ids");
				onUpdate(context, widgetManager, widgetids);
			}
		}
	}
	
	
	public static void actualizarWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		Log.i("WIDGET", "actualizar Widget");
		//LocationManager elmanager= (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		//Criteria loscriterios = new Criteria(); 
		//loscriterios.setAccuracy(Criteria.ACCURACY_FINE);
		//String mejorproveedor= elmanager.getBestProvider(loscriterios, true);
		//elmanager.getLastKnownLocation(mejorproveedor);
		
		
		String rdo = "";
		//rdo = rdo + "entra en el localizador: "+mejorproveedor+"\n";
		//Location pos = elmanager.getLastKnownLocation(mejorproveedor);
		Location pos = getLastKnownLocation(context);
		if(pos != null){
			rdo = rdo+"entra en posicionamiento\n";
			//Si se dispone de una  posición, se busca en la base de datos la parada mas cercana
			//se abre la bd y se pide la parada.
			BaseDeDatos bd = new BaseDeDatos(context , "bizkaiaTransportesApp", null, 1);
			String datos = bd.paradaMasCercana(pos.getLatitude(), pos.getLongitude());
			if(datos != null){
				//Si hay una parada cercana,  
				rdo = "Parada mas cercana: \n"+datos;
			}else{
				rdo= rdo + "datos es null\n";
			}
			
		}else{
			rdo = rdo + "pos es null\n";
		}
		
		RemoteViews elementosgraficos = new RemoteViews(context.getPackageName(), R.layout.widget_interface);
		Intent intent = new Intent("eggop.DAS.bizkaiatransportesapp.WidgetConfiguracion");
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, 	intent, PendingIntent.FLAG_UPDATE_CURRENT);
		elementosgraficos.setTextViewText(R.id.etiqWidget, rdo);
		elementosgraficos.setOnClickPendingIntent(R.id.etiqWidget, pendingIntent);
		appWidgetManager.updateAppWidget(appWidgetId, elementosgraficos);
	}
	
	
	private static Location getLastKnownLocation(Context context) {
		LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = mLocationManager.getProviders(true);
		Location bestLocation = null;
		for (String provider : providers) {
			Location l = mLocationManager.getLastKnownLocation(provider);
			//Log.d("last known location, provider: %s, location: %s", provider, l);

			if (l == null) {
				continue;
			}
			if (bestLocation == null|| l.getAccuracy() < bestLocation.getAccuracy()) {
				// ALog.d("found best last known location: %s", l);
				bestLocation = l;
			}
		}
		if (bestLocation == null) {
			return null;
		}
		return bestLocation;
	}
	
}
