package com.example.dylosusbdatatransferapp.notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.dylosusbdatatransferapp.R;
import com.example.dylosusbdatatransferapp.service.AQMResultsFeedService;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoopBackEventHandlerActivity extends Activity{
	
	private TextView textView  = null;
	private Button byes = null;
	private Button bno = null;
	private boolean isGloballyBad;
	private boolean[] deviceIndividualBadness;
	private String[] deviceNames;
	private String TAG = "LoopBackEventHandlerActivity";
	private String timeAsString;
	private String predValues;
	private static final double EPA_STANDARD_THRESHOLD_PM25 = 2.48491;
	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.result);
	    
	    textView =(TextView)findViewById(R.id.textView1);
	    //get the extra from Intent
	    isGloballyBad = this.getIntent().getBooleanExtra(AQMResultsFeedService.PUT_EXTRA_IS_GLOBALLY_BAD_BOOL, true);
	    
	    deviceIndividualBadness = this.getIntent().getBooleanArrayExtra(AQMResultsFeedService.PUT_EXTRA_DEVICE_INDIV_BADNESS_BOOLARR);
	    deviceNames = this.getIntent().getStringArrayExtra(AQMResultsFeedService.PUT_EXTRA_DEVICE_NAMES_STRARR);
	    timeAsString = this.getIntent().getStringExtra(AQMResultsFeedService.PUT_EXTRA_TIME_AS_STRING);
	    predValues = this.getIntent().getStringExtra(AQMResultsFeedService.PUT_EXTRA_PREDICTION_VALUES);
	    Calendar now = Calendar.getInstance();
	    try {
			now.setTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(timeAsString));
		} catch (ParseException e) {
			Log.d(TAG, "Exception in onCreate"+e.getLocalizedMessage());
		}
	    int hours_now = now.get(Calendar.HOUR_OF_DAY);
	    String badDevicesCollated = "";
	    for (int i=0; i<deviceNames.length;i++){
	    	boolean check = true;
	    	check = check && deviceIndividualBadness[i];
	    	
	    	
	    	if(check){
	    		badDevicesCollated = badDevicesCollated + deviceNames[i] + " , ";
	    		Log.d(TAG,String.valueOf(deviceIndividualBadness[i]));
	    		Log.d(TAG,badDevicesCollated);
	    	}
	    }
	    if (isGloballyBad && (hours_now>15 && hours_now<18)){
	    	textView.setText("The air quality is globally bad at your home around " + timeAsString +" ; Why don't you go outside and play.  Did you leave?");
	    }else if (isGloballyBad && !(hours_now>15 && hours_now<18)){
	    	textView.setText("The air quality is globally bad at your home around " + timeAsString +" ; Please turn on the HVAC filter.  Did you switch on?");
	    }else if (!isGloballyBad){
	    	textView.setText("The air quality is locally bad at your home around " + timeAsString +" Why don't you go away from area where " + badDevicesCollated + " is/are located. Did you leave?");
	    }
	    
	    String[] predArray = predValues.substring(1).split("\\|");
	    textView.append("\r\n");
	    for (int i=0; i<deviceNames.length;i++){
	    	textView.append("Predicted value for next 1 hour 5 minutes for "+deviceNames[i] +" is " + predArray[i] +" in ln(micro gram/ cubic meter) units ");
	    	textView.append("\r\n");
	    }
	    
	    byes = (Button) findViewById(R.id.yesButton);
	    
		byes.setOnClickListener(new OnClickListener() {
 
			public void onClick(View arg0) {
				String displayText = "Thank you !!!";
		    	textView.setText(displayText);
			}
		});
		
		 bno = (Button) findViewById(R.id.noButton);
		    
		 bno.setOnClickListener(new OnClickListener() {
	 
				public void onClick(View arg0) {
					String displayText = "No worries !!!";
			    	textView.setText(displayText);
				}
			});
	      
	    
	  }
	  /**For handling click event of Quit button
	     * @param v
	     */
	    public void moveBackground(View v){   	
	    	System.runFinalization();
	    	System.exit(0);
	    	
	    }
	     

}
