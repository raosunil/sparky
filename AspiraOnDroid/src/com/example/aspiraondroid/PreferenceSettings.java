package com.example.aspiraondroid;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PreferenceSettings extends PreferenceFragment {
	public static final String CONFIG_NAME = "pref";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(CONFIG_NAME);
	    getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
		addPreferencesFromResource(R.xml.preference_settings);
	}
	
}