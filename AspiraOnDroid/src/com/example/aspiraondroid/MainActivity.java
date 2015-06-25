package com.example.aspiraondroid;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.aspiraondroid.service.MonitorService;
import com.example.aspiraondroid.service.SpirometerReadingService;
import com.example.aspiraondroid.view.ComplexDynamicView;




public class MainActivity extends ActionBarActivity implements PlaceholderFragment.Callback {
	
	private ComplexDynamicView gameView = null;
	private TextView nextReading= null;
	public static final int REFRESH = 1;
	private Handler guiRefresher;
	private final String TAG = "MainActivity";
	public static final int VENDOR_ID = 1027;
	public static final int PRODUCT_ID = 24577;
	private static final String ACTION_USB_PERMISSION =
		    "com.android.example.USB_PERMISSION";
	private PendingIntent mPermissionIntent = null;
	//public static final String PREFS_NAME = "DylosPrefs";
	public static final String SELECTED_DEVICE = "selecteddevice";
	private static UsbDevice tmpdevice;
	private SharedPreferences prefs;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(getApplicationContext(), PreferenceSettings.CONFIG_NAME, MODE_PRIVATE, R.xml.preference_settings, false);
		prefs = getApplicationContext().getSharedPreferences(PreferenceSettings.CONFIG_NAME, Context.MODE_PRIVATE);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, SetPreferenceActivity.class);
			startActivityForResult(intent, 0);
			return true;
		}else if (id == R.id.help_menu){
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, HelpMenuActivity.class);
			startActivityForResult(intent, 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	public void onCreateGetComplexView(View v) {
		// TODO Auto-generated method stub
		gameView = (ComplexDynamicView)v;
		guiRefresher = new Handler(){
			public void handleMessage(Message msg){
				if(msg.what == REFRESH){
					gameView.postInvalidate();
				}
				super.handleMessage(msg);
			}
		};

		gameView.setCallbackHandler(guiRefresher);
		Thread t = new Thread(gameView);
		t.setDaemon(true);
		t.start();
		
	}

	public void callAction(Intent intent){
		StringBuilder logMessage = new StringBuilder("MainActivity Found USB Device");
		boolean isConnected = false;
		
		UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
		HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		//int count = 0;
		while(deviceIterator.hasNext()){
			isConnected = true;
			tmpdevice = deviceIterator.next();
			
			//Log.d(TAG, "Found USB Device inside loop: "+tmpdevice.toString());
			if (tmpdevice.getVendorId()==VENDOR_ID && tmpdevice.getProductId()==PRODUCT_ID
					){
				try{
						
					UsbEndpoint input = null;
					UsbInterface usbInterface = tmpdevice.getInterface(0);
					for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
						logMessage.append(" Connected successfully ");
						//logMessage.append("||"+tmpdevice.toString()+"||");
						if (usbInterface.getEndpoint(i).getDirection() ==
								UsbConstants.USB_DIR_IN) {
								input = usbInterface.getEndpoint(i);
						}
					}
					
					manager.requestPermission(tmpdevice, mPermissionIntent);
					if (input!=null){
						Toast.makeText(this, logMessage, Toast.LENGTH_LONG).show();
						intent.putExtra(SELECTED_DEVICE, tmpdevice);
						DeviceHolder.getInstance().setDevice(tmpdevice);
						startService(intent);
						//new Thread(new UsbCommunciation(manager,tmpdevice)).start();
						break;
					}

				}catch(Exception e){
					Log.d(TAG, e.getLocalizedMessage());
					Log.d(TAG, e.getLocalizedMessage());
					//new LogToServerTask().execute("http://raostravelogue.com/roller-services/xmlrpc",e.getLocalizedMessage());
					}
			}else{
				logMessage.replace(0, logMessage.length(), "Error finding AQM Device");
				Toast.makeText(this, logMessage, Toast.LENGTH_LONG).show();
			}
			
		}
		
		try{
			if(!isConnected){
				logMessage.replace(0, logMessage.length(), "Device not connected");
				Toast.makeText(this, logMessage, Toast.LENGTH_LONG).show();
			}
		}catch(Exception e){
			Log.d(TAG, e.getLocalizedMessage());
			Log.d(TAG, e.getLocalizedMessage());
			//new LogToServerTask().execute("http://raostravelogue.com/roller-services/xmlrpc",e.getLocalizedMessage());
			
		}

	}

	private String determineNextReading() {
		String nextReadingConfigString = "";
		String retValue="";
		nextReadingConfigString = prefs.getString("asthmaglobals.config.alertInfo.spiroReadingTime", "");
		String[] nextReadingArray = nextReadingConfigString.split(",");
		Calendar cal = Calendar.getInstance();
		int hours = cal.get(Calendar.HOUR_OF_DAY);
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
		cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(nextReadingHour.substring(0,2)));
		cal.set(Calendar.MINUTE, Integer.valueOf(nextReadingHour.substring(2,nextReadingHour.length())));
		String am_pm = cal.get(Calendar.HOUR_OF_DAY)>=12?"PM":"AM";
		retValue = String.format("%02d",cal.get(Calendar.HOUR)) + ":"+ String.format("%02d", cal.get(Calendar.MINUTE)) 
				+ " " +  am_pm;
		SharedPreferences.Editor editor = prefs.edit();
		//Toast.makeText(getApplicationContext(), String.valueOf(cal.getTimeInMillis()), Toast.LENGTH_LONG).show();
		editor.putString("asthmaglobals.nextReadingTimeinMillis", String.valueOf(cal.getTimeInMillis()));
		editor.commit();
		return retValue;
	}
	
	@Override
	public void monitorServiceClicked(View v) {
		
		// Is the toggle on?
	    boolean on = ((ToggleButton) v).isChecked();
	    Intent i = new Intent(getApplicationContext(), MonitorService.class);
	    if (on) {
        	callAction(i); 
	    }else{
	    	stopService(i);
	    }
	    
	}

	@Override
	public void onCreateGetNextReadingTextView(View v) {
		nextReading = (TextView)v;
		nextReading.setText(determineNextReading());
		if (Long.valueOf(prefs.getString("asthmaglobals.nextReadingTimeinMillis", "0")) > 0){
			Intent i = new Intent(getApplicationContext(), SpirometerReadingService.class);
			startService(i);
		}
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		nextReading.setText(determineNextReading());
	}

}
