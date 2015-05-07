package eggop.DAS.bizkaiatransportesapp;

import java.util.LinkedList;

import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class BaseDeDatos extends SQLiteOpenHelper{

	/**
	 * Constante para determinar la distancia minima del usuario a una parada
	 * para que pueda considerarse cerca. Actualmente a 1km para tener un 
	 * rango amplio.
	 * */
	private static final float CTE_DISTANCIA_MINIMA=1000;
	private SQLiteDatabase db=getWritableDatabase();
	
	public BaseDeDatos(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	/**
	 * Creación de la base:
	 * 		tabla Lineas: Guarda las lineas tanto de metro como de bus.
	 * 		tabla Parada: Guarda las paradas existentes con su dirección y coordenadas
	 * 		tabla recorridos: Guarda para cada parada, las lineas a las que pertenece, el orden en la linea
	 * 			y los minutos hasta la siguiente parada.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("CREATING DATABASE");
		db.execSQL("CREATE TABLE Lineas ('tipo' TEXT, 'numero' TEXT, 'Descripcion' TEXT, 'frecuencia' TEXT)");//en lugar de boolean fav es si=1, no=0
		db.execSQL("CREATE TABLE paradas ('id' TEXT PRIMARY KEY, 'direccion' TEXT, 'lat' TEXT, 'lon' TEXT)");
		db.execSQL("CREATE TABLE recorridos ('Lnum' TEXT, 'Pid' TEXT, 'numParLinea' INTEGER, 'tiempo' INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}
	
	/**
	 * Devuelve un cursor con todas las lineas existentes en la bd.
	 * Es necesario cerrar el cursor tras su uso.
	 * @return
	 */
	public Cursor listaLineas(){
		Cursor c = db.rawQuery("SELECT tipo, numero, descripcion FROM Lineas", null);
		return c;
	}
	
	public Cursor infoLinea(String linea){
		Cursor c = db.rawQuery("SELECT tipo, Descripcion, frecuencia from Lineas where numero='"+linea+"'", null);
		return c;
	}
	
	/**
	 * Devuelve el numero de minutos que hay entre los servicios.
	 * @param tipo el tipo de transporte que es (m=metro; b=bus)
	 * @param linea numero correspondiente a la linea de transporte.
	 * @return 
	 */
	public String frecuenciaDeLinea(String tipo, String linea){
		String rdo= "";
		Cursor c = db.rawQuery("SELECT frecuencia FROM Lineas WHERE tipo='"+tipo+"' AND numero='"+linea+"'", null);
		if(c.moveToFirst()){
			rdo = c.getString(c.getColumnIndex("frecuencia"));
		}
		c.close();
		return rdo;
	}

	/**
	 * devuelve una lista de las paradas de la linea indicada en 'linea' 
	 * ordenada por el orden en el que se suceden las paradas en la linea.
	 * @param linea indicativo de la linea
	 * @return
	 */
	public LinkedList<String> listaParadasDeLinea(String linea) {
		LinkedList<String> rdo = new LinkedList<String>();
		Cursor c = db.rawQuery("SELECT id, direccion FROM paradas INNER JOIN recorridos ON id=Pid WHERE Lnum='"+linea+"' ORDER BY numParLinea", null);
		c.move(-1);
		while(c.moveToNext()){
			rdo.add(c.getString(c.getColumnIndex("id"))+" - "+c.getString(c.getColumnIndex("direccion")));
		}
		c.close();
		return rdo;
	}

	/**
	 * Devuelve el booleano indicando si el usuario ha marcado esa linea
	 * como favorita.
	 * @param linea indicativo de la linea
	 * @return
	 */
	public boolean esFavorita(String linea) {
		boolean rdo = false;
		Cursor c = db.rawQuery("SELECT Fav FROM lineas WHERE numero = '"+linea+"'", null);
		System.out.println("SELECT Fav FROM lineas WHERE numero = '"+linea+"'");
		if(c.moveToFirst()){
			rdo = c.getInt(c.getColumnIndex("Fav"))==1;
		}
		c.close();
		return rdo;
	}

	/**
	 * cambia el boolean que indica que el usuario tiene una preferencia
	 * por esa linea
	 * @param linea indicativo de la linea
	 * @param isChecked preferencia de la linea
	 */
	public void marcarFavorita(String linea, boolean isChecked) {
		int b;
		if(isChecked){
			b=1;
		}else{
			b=0;
		}
		ContentValues con = new ContentValues();
		con.put("Fav", b);
		db.update("Lineas", con, "numero"+"='"+linea+"'", null);
	}

	/**
	 * devuelve los datos mas significantes de la linea, en caso de que alguna
	 * de las paradas en alguna de las líneas marcadas como favoritas por el 
	 * usuario esten dentro del rango.
	 * @param latitude latitud actual del usuario
	 * @param longitude longitud actual del usuario
	 * @return un String compuesto de la dirección de la parada, la linea a la 
	 * que pertenece y la frecuencia de esa linea. la estructura de este estring es el siguiente:
	 * direc - linea - frec;
	 */
	public String paradaMasCercanaAMinimaDistancia(double latitude, double longitude) {
		String rdo=null;
		
		Cursor c = db.rawQuery("SELECT lat, lon, direccion, numero, frecuencia FROM (paradas INNER JOIN recorridos ON id=Pid) INNER JOIN Lineas ON Lnum=numero", null);
		c.move(-1);
		double lat, lon;
		float minDist=Float.MAX_VALUE;
		String direc, linea, frec;
		float[] results = {0};
		
		while(c.moveToNext()){
			lat = Double.parseDouble(c.getString(c.getColumnIndex("lat")));
			lon = Double.parseDouble(c.getString(c.getColumnIndex("lon")));
			Location.distanceBetween(latitude, longitude, lat, lon, results);
			if(results[0]<minDist && results[0] <= CTE_DISTANCIA_MINIMA){
				minDist=results[0];
				direc = c.getString(c.getColumnIndex("direccion"));
				linea = c.getString(c.getColumnIndex("numero"));
				frec = c.getString(c.getColumnIndex("frecuencia"));
				rdo = direc+" - "+linea+" - "+frec;
			}
		}
		
		return rdo;
	}
	
	public String paradaMasCercana(double latitude, double longitude){
			String rdo="";
			
			Cursor c = db.rawQuery("SELECT lat, lon, direccion, numero, frecuencia FROM (paradas INNER JOIN recorridos ON id=Pid) INNER JOIN Lineas ON Lnum=numero", null);
			c.move(-1);
			double lat, lon;
			float minDist=Float.MAX_VALUE;
			String direc, linea, frec;
			float[] results = new float[1];
			Log.i("actualizar Widget", "comienza while");
			while(c.moveToNext()){
				lat = Double.parseDouble(c.getString(c.getColumnIndex("lat")));
				lon = Double.parseDouble(c.getString(c.getColumnIndex("lon")));
				Log.i("actualizar Widget", "parada="+c.getString(c.getColumnIndex("direccion")));
				Log.i("actualizar Widget", "latitude="+latitude+", longitude="+longitude);
				Log.i("actualizar Widget", "lat="+lat+", lon="+lon);
				Location.distanceBetween(latitude, longitude, lat, lon, results);
				Log.i("actualizar Widget", "distancia="+results[0]+", min="+minDist);
				if(results[0] < minDist){
					Log.i("actualizar Widget", "entra en if");
					minDist=results[0];
					direc = c.getString(c.getColumnIndex("direccion"));
					linea = c.getString(c.getColumnIndex("numero"));
					frec = c.getString(c.getColumnIndex("frecuencia"));
					rdo = direc+" - "+linea+" - "+frec;
				}
			}
			
			return rdo;
	}

	/**
	 * Devuelve el tipo de transporte que recorre la linea indicada
	 * @param linea
	 * @return
	 */
	public String tipoTransporte(String linea) {
		String rdo = "";
		Cursor c = db.rawQuery("SELECT tipo FROM lineas WHERE numero = '"+linea+"'", null);
		if(c.moveToFirst()){
			rdo = c.getString(c.getColumnIndex("tipo"));
		}
		c.close();
		return rdo;
	}
	
	/**
	 * Devuelve la descripción asociada a la linea.
	 * @param linea
	 * @return
	 */
	public String descripcionLinea(String linea) {
		String rdo = "";
		Cursor c = db.rawQuery("SELECT descripcion FROM lineas WHERE numero = '"+linea+"'", null);
		System.out.println("descripcionLinea: "+"SELECT descripcion FROM lineas WHERE numero = '"+linea+"'");
		if(c.moveToFirst()){
			rdo = c.getString(0);
		}
		c.close();
		return rdo;
	}
	
	public void anadirLinea(String linea, String descripcion, String tipo, String frecuencia){
		String sql = "INSERT INTO Lineas( 'tipo', 'numero', 'descripcion', 'frecuencia') VALUES ('"+tipo+"', '"+linea+"', '"+descripcion+"', '"+frecuencia+"')";
		//Cursor c = db.rawQuery(sql, null);
		try{
			db.execSQL(sql);
		}catch(SQLException e){
			Log.e("BaseDeDatos", "anadirLinea: "+sql);
			e.printStackTrace();
		}
	}

	public void anadirParada(String id, String direccion, String lat, String lon) {
		Cursor c = db.rawQuery("SELECT id FROM paradas WHERE id='"+id+"'", null);
		//comprobamos si la parada ya existe
		if(!c.moveToFirst()){
			String sql = "INSERT INTO paradas('id', 'direccion', 'lat', 'lon') VALUES ('"+id+"', '"+direccion+"', '"+lat+"', '"+lon+"')";
//			db.rawQuery(sql, null);
			try{
				db.execSQL(sql);
			}catch(SQLException e){
				Log.e("BaseDeDatos", "anadirParada: "+sql);
				e.printStackTrace();
			}
		}
	}

	public void anadirRecorrido(String lNum, String pId, String numParLinea) {
		String sql = "INSERT INTO recorridos ('Lnum', 'Pid', 'numParLinea', 'tiempo') VALUES ('"+lNum+"', '"+pId+"', "+numParLinea+", 5)";
		try{
			db.execSQL(sql);
		}catch(SQLException e){
			Log.e("BaseDeDatos", "anadirRecorrido: "+sql);
			e.printStackTrace();
		}
//		db.rawQuery(sql, null);
	}

	public void borrarRecorridos(String pLinea) {
		String sql = "DELETE FROM recorridos WHERE Lnum = '"+pLinea+"'";
		try{
			db.execSQL(sql);
		}catch(SQLException e){
			Log.e("BaseDeDatos", "borrarRecorridos: "+sql);
			e.printStackTrace();
		}
//		db.rawQuery(sql, null);
	}

	public void borrarLinea(String pLinea) {
		String sql = "DELETE FROM Lineas WHERE numero = '"+pLinea+"'";
		try{
			db.execSQL(sql);
		}catch(SQLException e){
			Log.e("BaseDeDatos", "borrarLinea: "+sql);
			e.printStackTrace();
		}
//		db.rawQuery(sql, null);
	}

	public LatLng obtenerPosicionParada(String idParada) {
		LatLng rdo = null;
		String sql = "SELECT lat, lon FROM paradas WHERE id='"+idParada+"'";
		Cursor c = db.rawQuery(sql, null);
		if(c.moveToFirst()){
			Double lat = c.getDouble(c.getColumnIndex("lat"));
			Double lon = c.getDouble(c.getColumnIndex("lon"));
			rdo = new LatLng(lat, lon);
		}
		return rdo;
	}
	
}
