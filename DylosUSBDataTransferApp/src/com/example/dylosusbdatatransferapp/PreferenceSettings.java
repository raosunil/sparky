package com.example.dylosusbdatatransferapp;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class PreferenceSettings extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_settings);
		//PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
	}
}