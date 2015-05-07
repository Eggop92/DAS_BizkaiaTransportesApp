package eggop.DAS.bizkaiatransportesapp;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity {
	
	private double latParada;
	private double lonParada;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		Intent i = getIntent();
		latParada = i.getExtras().getDouble("lat");
		lonParada = i.getExtras().getDouble("lon");
		Log.i("MAPA", "latParada="+latParada+", lonParada="+lonParada);
		double latCentrado = latParada;
		double lonCentrado = lonParada;
		GoogleMap mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa)).getMap();
		mapa.addMarker(new MarkerOptions()
				.position(new LatLng(latParada, lonParada))
				.title("Parada")
		);
		Location posicion = getLastKnownLocation(this);
		if(posicion!=null){
			mapa.addMarker(new MarkerOptions()
				.position(new LatLng(posicion.getLatitude(), posicion.getLongitude()))
				.title("Tu")
			);
			latCentrado = (latCentrado+posicion.getLatitude())/2;
			lonCentrado = (lonCentrado+posicion.getLongitude())/2;
		}
		CameraPosition poscam = new CameraPosition.Builder()
				.target(new LatLng(latCentrado, lonCentrado))
				.zoom(12)
				.build();
		CameraUpdate act = CameraUpdateFactory.newCameraPosition(poscam);
		mapa.animateCamera(act);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
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
