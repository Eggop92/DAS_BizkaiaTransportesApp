package eggop.DAS.bizkaiatransportesapp;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class RegistroGCM extends AsyncTask<String ,Void, String> {

	public static final String GCM_ID = "GCM_ID";
	public static final String REGISTRADO_GCM = "registradoGCM";
	public static final String URL = "http://"+AccesoBDExterna.ip+"/DAS/BizkaiaTransportesApp/registro.php";
	private static final String SENDER_ID = "152734451458";
	private Context context;
	
	public RegistroGCM(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {
		
		
		
		String msg="";
		try{
			
			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
			String regid = gcm.register(SENDER_ID);
			AccountManager accountManager = AccountManager.get(context);
			String cuenta = accountManager.getAccounts()[0].name;
			Log.i("info", "REgistroGCM.regid="+regid);
			Log.i("info", "REgistroGCM.cuenta="+cuenta);
			
			
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
			HttpConnectionParams.setSoTimeout(httpParameters, 15000);
			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
			parametros.add(new BasicNameValuePair(REGISTRADO_GCM, cuenta));
			parametros.add(new BasicNameValuePair(GCM_ID, regid));
			HttpPost httppost = new HttpPost(URL);
			httppost.setEntity(new UrlEncodedFormEntity(parametros));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			JSONObject jsonObject = new JSONObject(result);
			if(jsonObject.has("OK")){
				Log.i("info", "REgistroGCM.has(OK)");
				Editor pref = PreferenceManager.getDefaultSharedPreferences(context).edit();
				pref.putBoolean(REGISTRADO_GCM, true);
				pref.putString(GCM_ID, regid);
				pref.putString("usuario", cuenta);
				pref.commit();
				msg = "OK";
			}else{
				Log.e("info", jsonObject.getString("ERROR"));
			}

		}catch(IOException ex){
			Log.e("info", "RegistroGCM.IOException ");
			ex.printStackTrace();
			msg = "Error: "+ex.getMessage();
		} catch (JSONException e) {
			Log.e("info", "RegistroGCM.JSONException");
			e.printStackTrace();
		}
		return msg;
	}


}
