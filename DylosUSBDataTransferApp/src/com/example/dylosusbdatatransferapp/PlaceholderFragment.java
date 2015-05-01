package com.example.dylosusbdatatransferapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;



/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

	private Switch feedSwitch = null;
	private ImageView imgView = null;
	private Callback callback;
	
	 public interface Callback {
	        public void onSwitchButtonClicked(boolean callService);
	        public void onCreateGetImgView(View v);
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
	    
	    feedSwitch = (Switch) rootView.findViewById(R.id.sw_AnalyticFeed);
	  
	  //set the switch to ON 
	    feedSwitch.setChecked(false);
	    //attach a listener to check for changes in state
	    feedSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	    	@Override
	    	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	    		if(isChecked){
	    			callback.onSwitchButtonClicked(true);
	    		}else{
	    			callback.onSwitchButtonClicked(false);
	    		}
	    	}
	    });
	    
	    imgView = (ImageView) rootView.findViewById(R.id.pulseview);  
	    callback.onCreateGetImgView(imgView);

		return rootView;
	}
	

	
	


	
}

