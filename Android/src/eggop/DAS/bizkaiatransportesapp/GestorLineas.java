package eggop.DAS.bizkaiatransportesapp;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

public class GestorLineas {
	
	private static GestorLineas gl;
	
	private GestorLineas(){}
	
	public static GestorLineas getGestorLineas(){
		if(gl == null){
			gl = new GestorLineas();
		}
		return gl;
	}

	public String[] getInfoLinea(String linea, Activity activity) {
		Log.i("GestorLineas", "getInfoLinea "+linea);
		// 0= tipo, 1= desc, 2= fav, 3= frec
		//inicializamos el array por si no se encuentra o se pierde la info de la linea
		String[] rdo = new String[4];
		rdo[0] = "";
		rdo[1] = "";
		rdo[2] = "";
		rdo[3] = "";
		//buscamos la info en la base de datos local
		BaseDeDatos db = new BaseDeDatos(activity, "bizkaiaTransportesApp", null, 1);
		Cursor c = db.infoLinea(linea);
		if(c.moveToFirst()){
			Log.i("GestorLineas", "entra en el moveToFirst");
			rdo[0] = c.getString(c.getColumnIndex("tipo"));
			rdo[1] = c.getString(c.getColumnIndex("Descripcion"));
			//Si esta indica sistematicamente que es favorita
			rdo[2] = "true";
			rdo[3] = c.getString(c.getColumnIndex("frecuencia"));
			
		}else{
			Log.i("GestorLineas", "entra en el else de moveToFirst");
			//Si no esta en local, se busca en la remota
			try {
				AccesoBDExterna abde = new AccesoBDExterna();
				abde.execute("infoLineas", linea);
				JSONObject json = abde.get();
				//miramos si ha devuelto un error
				if(json.has("ERROR")){
					Log.e("BDExterna", json.getString("ERROR"));
				}else{
					//Buscamos la linea con la info en el JSON
					JSONObject obj;
					if((obj = json.getJSONObject("Linea"))!=null){
						rdo[0] = obj.getString("tipo");
						rdo[1] = obj.getString("descripcion");
						rdo[2] = "false";
						rdo[3] = obj.getString("frecuencia");
					}else{
						Log.e("GestorLinea", "La Linea no se ha encontrado bien");
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		c.close();
		return rdo;
	}

	public LinkedList<String> listaParadasDeLinea(String linea,	Activity activity) {
		LinkedList<String> rdo;
		BaseDeDatos db = new BaseDeDatos(activity, "bizkaiaTransportesApp", null, 1);
		rdo = db.listaParadasDeLinea(linea);
		//Si no se ha encontrado ninguna parada (que una linea no tenga paradas no tiene sentido)
		if(rdo.isEmpty()){
			try {
				AccesoBDExterna abde = new AccesoBDExterna();
				abde.execute("ParadasLineas", linea);
				JSONObject json;
				json = abde.get();
				//miramos si ha devuelto un error
				if(json.has("ERROR")){
					Log.e("BDExterna", json.getString("ERROR"));
				}else{
					JSONObject o;
					JSONArray array = json.getJSONArray("Paradas");
					for(int i =0; i < array.length(); i++){
						o = array.getJSONObject(i);
						rdo.add(o.getInt("id")+" - "+o.getString("direccion"));
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return rdo;
	}

	public void marcarFavorita(Activity activity, String pLinea, boolean checked) {
		try {
			AccesoBDExterna abde = new AccesoBDExterna();
			String email = PreferenceManager.getDefaultSharedPreferences(activity).getString("usuario", "");
			abde.execute("MarcarLinea", pLinea, email, checked+"");
			JSONObject json = abde.get();
			Log.i("marcarFavorito", "Pasa el get");
			//miramos si ha devuelto un error
			if(json.has("ERROR")){
				Log.i("marcarFavorito", "Entra en el error");
				Log.e("BDExterna", json.getString("ERROR"));
			}else{
				Log.i("marcarFavorito", "Entra en el else del error");
				BaseDeDatos bd = new BaseDeDatos(activity, "bizkaiaTransportesApp", null, 1);
				if(checked){
					Log.i("marcarFavorito", "Entra en el checked");
					if(json.has("Linea")){ //no debería de serlo, pero por si acaso
						Log.i("marcarFavorito", "Entra en hasLinea");
						JSONObject infoLinea = json.getJSONObject("Linea");
						String tipo, linea, descripcion, frecuencia;
						tipo = infoLinea.getString("tipo");
						linea = infoLinea.getString("numero");
						descripcion = infoLinea.getString("descripcion");
						frecuencia = infoLinea.getString("frecuencia");
						bd.anadirLinea(linea, descripcion, tipo, frecuencia);
					}
					
					if(json.has("Paradas")){
						Log.i("marcarFavorito", "Entra en hasParadas");
					JSONArray paradas = json.getJSONArray("Paradas");
						JSONObject parada;
						String id, direccion, lat, lon;
						for(int i =0; i<paradas.length(); i++){
							parada = paradas.getJSONObject(i);
							id = parada.getString("id");
							direccion = parada.getString("direccion");
							lat = parada.getString("lat");
							lon = parada.getString("lon");
							bd.anadirParada(id, direccion, lat, lon);
						}
					}
					
					if(json.has("Recorridos")){
						Log.i("marcarFavorito", "Entra en hasRecorridos");
						JSONArray recorridos = json.getJSONArray("Recorridos");
						JSONObject recorrido;
						String lNum, pId, numParLinea;
						for(int i=0; i<recorridos.length(); i++){
							recorrido = recorridos.getJSONObject(i);
							lNum = recorrido.getString("Lnum");
							pId = recorrido.getString("Pid");
							numParLinea = recorrido.getString("numParLinea");
							bd.anadirRecorrido(lNum, pId, numParLinea);
						}
					}
					bd.close();
				}else{
					//borra el favorito
					Log.i("marcarFavorito", "Entra en borrarFavorito");
					if(json.has("INFO")){
						Log.i("marcarFavorito", "Entra en hasInfo");
						bd.borrarRecorridos(pLinea);
						//no borro paradas por comodidas (SQL muy compleja)
						bd.borrarLinea(pLinea);
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public LatLng obtenerPosicionParada(Activity activity, String idParada) {
		LatLng rdo = null;
		BaseDeDatos db = new BaseDeDatos(activity, "bizkaiaTransportesApp", null, 1);
		rdo = db.obtenerPosicionParada(idParada);
		if(rdo == null){
			AccesoBDExterna abde = new AccesoBDExterna();
			abde.execute("PosParada", idParada);
			JSONObject jsonObject;
			try {
				jsonObject = abde.get();
				if(jsonObject.has("ERROR")){
					Log.e("BDExterna", jsonObject.getString("ERROR"));
				}else{
					if(jsonObject.has("lat") && jsonObject.has("lon")){
						rdo = new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lon"));
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
		return rdo;
	}

	

}
