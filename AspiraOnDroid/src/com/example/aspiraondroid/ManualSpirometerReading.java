package com.example.aspiraondroid;



import java.util.Date;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aspiraondroid.dao.SpirometerDBHandler;
import com.example.aspiraondroid.model.SpirometerReading;
import com.google.gson.Gson;
/**
 * @author Sunil Rao
 */
public class ManualSpirometerReading extends Activity{
	
	private View rootlayout = null;
	private EditText pefValue = null;
	private EditText fevValue = null;
	private CheckBox symptoms = null;
	private SharedPreferences prefs;
	private  SpirometerDBHandler dbHandler = null;
	private String uri;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		try{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_layout);
		rootlayout = findViewById(R.id.manualRootLayout);
		prefs = getApplicationContext().getSharedPreferences(PreferenceSettings.CONFIG_NAME, Context.MODE_PRIVATE);
		if (dbHandler==null)
			dbHandler = new SpirometerDBHandler(getApplicationContext());
		pefValue = (EditText)rootlayout.findViewById(R.id.pefValue);
		fevValue =  (EditText)rootlayout.findViewById(R.id.fevValue);
		symptoms = (CheckBox)rootlayout.findViewById(R.id.symptomresponse);
		uri = prefs.getString("url", "http://lead2.poly.asu.edu:8090/AQMEcho/aqmecho");
		}catch (Exception e){
			Log.d("ManualSpirometerReading", e.getLocalizedMessage());
		}
	}
	
	public void executeCancel(View v){
		finish();
	}
	
	
	public void executeSubmit(View v){
		boolean retValue = validate();
		String deviceId = prefs.getString("asthmaglobals.config.deviceID", "1");
		String patientId = prefs.getString("asthmaglobals.config.patientID", "1");
		if(retValue){
			SpirometerReading sp  = new SpirometerReading(deviceId, patientId, new Date(), "-1",  true, pefValue.getText().toString(), fevValue.getText().toString(), "0", "0", symptoms.isChecked());
			dbHandler.Add_SpmResults(sp);
			List retList = dbHandler.Get_SPMResults();
			if(retList!=null){
				//Toast.makeText(getApplicationContext(), "Size of retList "+retList.size(), Toast.LENGTH_LONG).show();
			}
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("asthmaglobals.teaseSnapTimeinMillis",String.valueOf(new Date().getTime()));
			editor.putString("asthmaglobals.config.animation.currMood", "sleepy");
			editor.putString("asthmaglobals.config.animation.changeMood2","happy");
			editor.commit();
			SpirometerReading[] spArray = {sp};
			new PushServerTask().execute(spArray);
			/*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(intent);*/
			finish();
		}

	}

	private boolean validate() {
		boolean retValue = false;

		try{

			int pef_minVal = Integer.valueOf(prefs.getString("asthmaglobals.config.minValues.PEFValue", "0"));
			int pef_maxVal = Integer.valueOf(prefs.getString("asthmaglobals.config.maxValues.PEFValue", "0"));

			double fev_minVal = Double.valueOf(prefs.getString("asthmaglobals.config.minValues.FEVValue", "0.0"));
			double fev_maxVal = Double.valueOf(prefs.getString("asthmaglobals.config.maxValues.FEVValue", "0.0"));


			if (pefValue.getText().length()==0 || 
					Integer.valueOf(pefValue.getText().toString()) > pef_maxVal ||
					Integer.valueOf(pefValue.getText().toString()) < pef_minVal){
				retValue = false;
				Toast.makeText(getApplicationContext(), "Error in PEF Value", Toast.LENGTH_LONG).show();
				pefValue.setError("Error in PEF Value");
				return retValue;
			}

			if (fevValue.getText().length()==0 || 
					Double.valueOf(fevValue.getText().toString()) > fev_maxVal ||
					Double.valueOf(fevValue.getText().toString()) < fev_minVal){
				retValue = false;
				Toast.makeText(getApplicationContext(), "Error in FEV Value", Toast.LENGTH_LONG).show();
				fevValue.setError("Error in FEV Value");
				return retValue;
			}
			//Passed all hurdles
			retValue = true;



		}catch (Exception e){
			retValue = false;
		}
		return retValue;

	}
	
	@Override
	public void onDestroy(){
		if(dbHandler!=null){
			dbHandler.close();
			dbHandler = null;
		}
		super.onDestroy();
	}
	
	private class PushServerTask extends AsyncTask<SpirometerReading, Void, Void> {
	    

		@Override
		protected Void doInBackground(SpirometerReading... params) {
			try{
			String uri = "http://lead2.poly.asu.edu:8090/AQMEcho/aqmecho";
			int timeOutMs = 10000; 
					if (prefs!=null){
						uri = prefs.getString("url", "http://lead2.poly.asu.edu:8090/AQMEcho/aqmecho");
					}
					SpirometerReading sp = params[0];
					String json =  new Gson().toJson(sp);
					HttpPost httpPost = new HttpPost(uri);
					httpPost.setEntity(new StringEntity(json));
					httpPost.setHeader("Accept", "application/json");
					httpPost.setHeader("Content-type", "application/json");

					HttpParams myParams = new BasicHttpParams();
					myParams.setParameter("jason", json);
					HttpConnectionParams.setConnectionTimeout(myParams, timeOutMs);
					HttpConnectionParams.setSoTimeout(myParams, timeOutMs);

					HttpClient httpclient = new DefaultHttpClient(myParams);
					httpclient.execute(httpPost);
			}
			catch (Exception e){
				Log.d("PushServerTask", e.getLocalizedMessage());
			}
			return null;
		}

	 }
}
