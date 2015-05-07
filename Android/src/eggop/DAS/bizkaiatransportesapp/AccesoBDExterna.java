package eggop.DAS.bizkaiatransportesapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class AccesoBDExterna extends AsyncTask<String, Void, JSONObject> {

	public static final String ip = ""; //IP SERVIDOR PHP
	
	private static final String index = "http://"+ip+"/DAS/BizkaiaTransportesApp/index.php";
	private static final String selLineas = "http://"+ip+"/DAS/BizkaiaTransportesApp/lineas.php";
	
	private String funcion;
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected JSONObject doInBackground(String... params) {
		JSONObject jsonObject = null;
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
			HttpConnectionParams.setSoTimeout(httpParameters, 15000);
			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			funcion = params[0];
			ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
			parametros.add(new BasicNameValuePair("FUNCION", funcion));
			String url = index;
			if(funcion.equals("selLineas")){
				url = selLineas;
			}else if(funcion.equals("infoLineas") || funcion.equals("ParadasLineas")){
				Log.i("AccesoBDExterna", "infoLineas: "+params[1]);
				url = selLineas;
				parametros.add(new BasicNameValuePair("LINEA", params[1]));
			}else if(funcion.equals("MarcarLinea")){
				url = selLineas;
				parametros.add(new BasicNameValuePair("LINEA", params[1]));
				parametros.add(new BasicNameValuePair("USUARIO", params[2]));
				parametros.add(new BasicNameValuePair("FAVORITO", params[3]));
			}else if(funcion.equals("PosParada")){
				url = selLineas;
				parametros.add(new BasicNameValuePair("ID_PARADA", params[1]));
			}
			Log.i("info", url);
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(parametros));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			jsonObject = new JSONObject(result);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
		return jsonObject;
	}

	protected void onPostExecute(JSONObject rdo) {
		/*try {
			if(funcion.equals("selLineas")){
				LinkedList<String> infoLineas = new LinkedList<String>();
				JSONArray array;
				if((array = rdo.getJSONArray("Lineas"))!=null){
					for (int i = 0; i < array.length(); i++) {
						JSONObject c = array.getJSONObject(i);
						String tipo = c.getString("tipo");
						String numero = c.getString("numero");
						String descripcion = c.getString("descripcion");
						//String frecuencia = c.getString("frecuencia");
						infoLineas.add(tipo+" - "+numero+"\n"+descripcion);
					}
					//VerLineas.
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
	};
	
}
