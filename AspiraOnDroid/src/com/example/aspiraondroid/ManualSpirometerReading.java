package com.example.aspiraondroid;



import java.util.Date;
import java.util.List;

import com.example.aspiraondroid.dao.SpirometerDBHandler;
import com.example.aspiraondroid.model.SpirometerReading;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class ManualSpirometerReading extends Activity{
	
	private View rootlayout = null;
	private EditText pefValue = null;
	private EditText fevValue = null;
	private CheckBox symptoms = null;
	private SharedPreferences prefs;
	private  SpirometerDBHandler dbHandler = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_layout);
		rootlayout = findViewById(R.id.manualRootLayout);
		prefs = getApplicationContext().getSharedPreferences(PreferenceSettings.CONFIG_NAME, Context.MODE_PRIVATE);
		dbHandler = new SpirometerDBHandler(this);
		pefValue = (EditText)rootlayout.findViewById(R.id.pefValue);
		fevValue =  (EditText)rootlayout.findViewById(R.id.fevValue);
		symptoms = (CheckBox)rootlayout.findViewById(R.id.symptomresponse);
	}
	
	public void executeCancel(View v){
		System.runFinalization();
		System.exit(0);

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
			editor.commit();

			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
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
		}
		super.onDestroy();
	}
}
