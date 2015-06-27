package com.example.aspiraondroid;


import android.app.Activity;
import android.os.Bundle;
/**
 * @author Sunil Rao
 */
public class SetPreferenceActivity extends Activity {
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 getFragmentManager().beginTransaction().replace(android.R.id.content,
				 new PreferenceSettings()).commit();
	 }

	}
