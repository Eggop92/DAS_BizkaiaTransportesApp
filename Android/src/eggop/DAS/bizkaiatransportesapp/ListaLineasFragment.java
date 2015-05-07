package eggop.DAS.bizkaiatransportesapp;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListaLineasFragment extends ListFragment {
	
	private LinkedList<String> lineas;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		cargarBaseDeDatosLocal();
	}

	private void cargarBaseDeDatosLocal() {
		//Cargamos la info de las lineas en la lista de la forma nos parece conveniente mostrar.
		String infoLinea;
		lineas = new LinkedList<String>();
		//abrimos la base de datos y pedimos el cursor con todas las lineas
		BaseDeDatos bd = new BaseDeDatos(getActivity(), "bizkaiaTransportesApp", null, 1);
		Cursor c = bd.listaLineas();
		//movemos el cursor hasta antes de la primera linea
		c.moveToPosition(-1);
		while(c.moveToNext()){
			//Por cada linea sacamos la información que necesitamos para poner en la lista
			infoLinea = c.getString(c.getColumnIndex("tipo"))+" - ";
			infoLinea = infoLinea+c.getString(c.getColumnIndex("numero"))+"\n";
			infoLinea = infoLinea+c.getString(c.getColumnIndex("Descripcion"));
			//lo añanadimos a un array para el adapter
			lineas.add(infoLinea);
		}
		c.close();
		
		lineas.add(getActivity().getString(R.string.masLineas));
		asignarAdaptador(lineas);
	}

	public void asignarAdaptador(LinkedList<String> pListaLineas) {
		//Creamos y asignamos el adapter al ListView del fragment.
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, pListaLineas);
		setListAdapter(adapter);
	}
	
	//Hacemos una interface para que todas las activities que vayan a usarlo tengan que tener estos metodos.
	public interface ListenerdeListaLineas {
		void onItemSeleccionado(String item);
	}
	
	//Nos aseguramos que aquellas interfaces que usan el Fragment tienen la interfaz.
	private ListenerdeListaLineas list;
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			list = (ListenerdeListaLineas) activity;
		} catch (ClassCastException e) {}
	}
	
	//Este es el método que se ejecuta al pulsar sobre cualquier elemento de la lista
	// y sera el que lance la peticion al otro fragment para que cambie su contenido
	// o se abra una nueva Activity con los detalles.
	//Tambien controla si ha de cargar mas lineas desde la base de datos externa
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//if(position == lineas.size()-1){
		if(lineas.get(position).equals(getActivity().getString(R.string.masLineas))){
			//Es el item de Cargar Mas lineas
			cargarBaseDatosExterna();
		}else{
			//Le pasamos al otro fragment solo el numero de linea
			list.onItemSeleccionado(lineas.get(position).split("\n")[0].split(" - ")[1].trim());
		}
	}

	private void cargarBaseDatosExterna() {
		AccesoBDExterna abde = new AccesoBDExterna();
		try {
			abde.execute("selLineas");
			JSONObject json= abde.get();
			if(json != null){
				LinkedList<String> infoLineas = new LinkedList<String>();
				JSONArray array;
				
				if(json.has("ERROR")){
					Log.e("BDExterna", json.getString("ERROR"));
				}else{
					if((array = json.getJSONArray("Lineas"))!=null){
						for (int i = 0; i < array.length(); i++) {
							JSONObject c = array.getJSONObject(i);
							String tipo = c.getString("tipo");
							String numero = c.getString("numero");
							String descripcion = c.getString("descripcion");
							//String frecuencia = c.getString("frecuencia");
							infoLineas.add(tipo+" - "+numero+"\n"+descripcion);
						}
						lineas = infoLineas;
						asignarAdaptador(infoLineas);
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
