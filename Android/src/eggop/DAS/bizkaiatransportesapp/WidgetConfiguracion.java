package eggop.DAS.bizkaiatransportesapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WidgetConfiguracion extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widget_configuracion);
		
		Log.i("WIDGET", "WidgetConfiguration.onCreate");
		
		Intent lanzador = getIntent();
		Bundle informacion = lanzador.getExtras();
		int id = informacion.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		
		Intent resultado = new Intent();
		resultado.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
		
		AppWidgetManager elWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
		Widget.actualizarWidget(getApplicationContext(), elWidgetManager, id);
		
		setResult(RESULT_OK, resultado);
		
		finish();
		
	}


}
