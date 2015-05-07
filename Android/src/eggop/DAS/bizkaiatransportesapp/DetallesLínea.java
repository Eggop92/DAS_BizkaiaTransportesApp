package eggop.DAS.bizkaiatransportesapp;

import java.util.LinkedList;

import com.google.android.gms.maps.model.LatLng;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

public class DetallesLínea extends Fragment {

	
	private String linea;
	private LinkedList<String> datos;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_detalle_lineas, container, false);
	}
	
	/**
	 * Cambia el contenido del Fragment, adecuandolo a lo que el usuario ha pulsado 
	 * en el Fragment de la lista.
	 * @param item
	 */
	public void actualizarContenido(String item) {
		//se cargan las vistas necesarias a tratar
		Switch favS = (Switch)getView().findViewById(R.id.favorita);
		TextView tituloTV = (TextView)getView().findViewById(R.id.Titulo);
		TextView descTV = (TextView)getView().findViewById(R.id.Descripcion);
		TextView frecTV = (TextView)getView().findViewById(R.id.frecuenciaValor);
		ListView listaLV = (ListView)getView().findViewById(R.id.listaParadas);
		
		//se abre la base de datos
		//BaseDeDatos db = new BaseDeDatos(getActivity(), "bizkaiaTransportesApp", null, 1);

		//inicialización de variables
		linea = item;
		String[] info = GestorLineas.getGestorLineas().getInfoLinea(linea, getActivity());
		//String tipo = GestorLineas.getGestorLineas().tipoTransporte(linea, getActivity());
		String tipo = info[0];
		//String desc = GestorLineas.getGestorLineas().descripcionLinea(linea, getActivity());
		String desc = info[1];
		//boolean esFav = GestorLineas.getGestorLineas().esFavorita(linea, getActivity());
		boolean esFav = info[2].equals("true");
		String transporte;
		switch(tipo.charAt(0)){
			case 'M': transporte = getString(R.string.metro);break;
			case 'B': transporte = getString(R.string.bus);break;
			case 'T': transporte = getString(R.string.tren);break;
			default: transporte =""; break;
		}
		
		//creación del listener que recoja los cambios realizados por el usuario
		//en su preferencia por la linea.
		favS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("DetallesLinea", "entra en el listener");
				//BaseDeDatos db = new BaseDeDatos(getActivity(), "bizkaiaTransportesApp", null, 1);
				GestorLineas.getGestorLineas().marcarFavorita(getActivity(), linea, ((Switch) v).isChecked());
			}
		});
		
		//colocación de los resultados en la interfaz
		favS.setChecked(esFav);
		tituloTV.setText(transporte+" "+linea);
		descTV.setText(desc);
		//frecTV.setText(GestorLineas.getGestorLineas().frecuenciaDeLinea(tipo, linea, getActivity())+" min");
		frecTV.setText(info[3]+" min");
		
		//colocación de la lista de paradas de la linea.
		datos = GestorLineas.getGestorLineas().listaParadasDeLinea(linea, getActivity());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, datos);
		listaLV.setAdapter(adapter);
		listaLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LatLng posParada = GestorLineas.getGestorLineas().obtenerPosicionParada(getActivity(), datos.get(position).split(" - ")[0]);
				Intent i = new Intent(getActivity(), MapActivity.class);
				Log.i("MAPA", "DETALLESLINEA: latParada="+ posParada.latitude+", lonParada="+posParada.longitude);
				i.putExtra("lat", posParada.latitude);
				i.putExtra("lon", posParada.longitude);
				startActivity(i);
			}
		});
	}
	
}
