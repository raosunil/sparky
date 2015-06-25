package com.example.aspiraondroid;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.aspiraondroid.view.ComplexDynamicView;



/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {


	private ToggleButton monitorButton = null;
	private Callback callback;
	private ComplexDynamicView gameView;
	private TextView nextReading;
	SharedPreferences prefs;

	
	 public interface Callback {
	        public void onCreateGetComplexView(View v);
	        public void onCreateGetNextReadingTextView(View v);
	        public void monitorServiceClicked(View v);
	    }

	    @Override
	    public void onAttach(Activity ac) {
	        super.onAttach(ac);
	        callback = (Callback)ac;
	    }

	    @Override
	    public void onDetach() {
	        callback = null;
	        super.onDetach();
	    }


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		prefs = ((MainActivity)callback).getApplicationContext().getSharedPreferences(PreferenceSettings.CONFIG_NAME, Context.MODE_PRIVATE);

		monitorButton = (ToggleButton) rootView.findViewById(R.id.btn_toggleMonitoring);
		monitorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				callback.monitorServiceClicked(v);				
			}
		});
		gameView = (ComplexDynamicView) rootView.findViewById(R.id.firstColumnView);
		callback.onCreateGetComplexView(gameView);
		nextReading = (TextView)rootView.findViewById(R.id.editTextNextReading);
		callback.onCreateGetNextReadingTextView(nextReading);
		//nextReading.setText(determineNextReading());
		nextReading.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.border2), getResources().getDrawable(R.drawable.border1),
                getResources().getDrawable(R.drawable.border2), getResources().getDrawable(R.drawable.border1));

		return rootView;
	}
/*
	private String determineNextReading() {
		String nextReadingConfigString = "";
		String retValue="";
		nextReadingConfigString = prefs.getString("asthmaglobals.config.alertInfo.spiroReadingTime", "");
		String[] nextReadingArray = nextReadingConfigString.split(",");
		Calendar cal = Calendar.getInstance();
		int hours = cal.get(Calendar.HOUR);
		int minutes = cal.get(Calendar.MINUTE);
		String nextReadingHour="";
		for (String str: nextReadingArray){
			if (hours < Integer.valueOf(str.substring(0, 2))) {
	            nextReadingHour = str;
	            break;
	        } else if (hours <= Integer.valueOf(str.substring(0, 2))
	            && minutes < Integer.valueOf(str.substring(2, str.length()))) {
	            //else if it is the same hour and min when compared to time right now
	            nextReadingHour = str;
	            break;
	        }
		}
		if (nextReadingHour.trim().length()==0){
			nextReadingHour = nextReadingArray[0];
			cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
		}
		
		cal.set(Calendar.HOUR, Integer.valueOf(nextReadingHour.substring(0,2)));
		cal.set(Calendar.MINUTE, Integer.valueOf(nextReadingHour.substring(2,nextReadingHour.length())));
		String am_pm = cal.get(Calendar.HOUR_OF_DAY)>12?"PM":"AM";
		retValue = String.valueOf(cal.get(Calendar.HOUR)) + ":"+ String.valueOf(cal.get(Calendar.MINUTE)) 
				+ " " +  am_pm;
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("asthmaglobals.nextReadingTimeinMillis", String.valueOf(cal.getTimeInMillis()));
		editor.commit();
		return retValue;
	}*/
	

	
	


	
}

