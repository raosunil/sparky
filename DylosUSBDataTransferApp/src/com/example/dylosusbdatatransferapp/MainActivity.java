package com.example.dylosusbdatatransferapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.dylosusbdatatransferapp.service.AQMResultsFeedService;
import com.example.dylosusbdatatransferapp.service.MonitorService;


public class MainActivity extends Activity implements PlaceholderFragment.Callback{
	
	private static final String TAG = "MainActivity";
	public static final int VENDOR_ID = 1027;
	public static final int PRODUCT_ID = 24577;
	private static final String ACTION_USB_PERMISSION =
		    "com.android.example.USB_PERMISSION";
	private PendingIntent mPermissionIntent = null;
	//public static final String PREFS_NAME = "DylosPrefs";
	public static final String SELECTED_DEVICE = "selecteddevice";
	private static UsbManager manager;
	private static UsbDevice tmpdevice;
	
	private String deviceId = null;
	private String userId = null;
	private String geoLatitude = null;
	private String geoLongitude = null;
	private String geoMethod = null;
	private String uri = null;
	private String periodic_delay = null;
	private String thread_size = null;
	private String grace_period = null;
	SharedPreferences prefs = null;
	
	private ImageView pulseView = null;
	
	
	
	
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        if (ACTION_USB_PERMISSION.equals(action)) {
		            synchronized (this) {
		                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

		                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
		                    if(device != null){
		                      //call method to set up device communication
		                   }
		                } 
		                else {
		                    Log.d(TAG, "permission denied for device " + device);
		                }
		            }
		        }
		    }
		};
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	   
	   
		//Request permission for accessing USB
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		registerReceiver(mUsbReceiver, filter);
		
		//Reading properties file
		Resources resources = this.getResources();
		AssetManager assetManager = resources.getAssets();
		// Read from the /assets directory
        try {
            InputStream inputStream = assetManager.open("dylossensorreader.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            
            deviceId = properties.getProperty("deviceId");
        	userId = properties.getProperty("userId");
        	geoLatitude = properties.getProperty("geoLatitude");
        	geoLongitude = properties.getProperty("geoLongitude");
        	geoMethod = properties.getProperty("geoMethod");
        	uri = properties.getProperty("url");
        	thread_size = properties.getProperty("thread_size");
        	grace_period = properties.getProperty("grace_period");
        	periodic_delay = properties.getProperty("periodic_delay");
        	
        	loadPrefs();
        	
           
        } catch (IOException e) {
            Log.d(TAG, "Failed to open properties file");
        }
        

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void loadPrefs(){
		prefs =
				PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	prefs.edit().putString("deviceId", deviceId);
    	prefs.edit().putString("userId", userId);
    	prefs.edit().putString("geoMethod", geoMethod);
    	prefs.edit().putString("url", uri);
    	prefs.edit().putString("geoLongitude", geoLongitude);
    	prefs.edit().putString("geoLatitude", geoLatitude);
    	prefs.edit().putString("thread_size", grace_period);
    	prefs.edit().putString("grace_period", thread_size);
    	prefs.edit().putString("periodic_delay",periodic_delay);
    	prefs.edit().apply();
	}
	
	
	/**Handler for onResultFeedClicked button
	 * @param v
	 */
	public void onResultFeedClicked(View v){
		
		Intent i = new Intent(getApplicationContext(),AQMResultsListViewActivity.class);
		startActivity(i);
		
	}
	
	/**Handler for onToggleClicked button
	 * @param v
	 */
	public void onToggleClicked(View v){
		// Is the toggle on?
	    boolean on = ((ToggleButton) v).isChecked();
	   
	    Intent i = new Intent(getApplicationContext(), MonitorService.class);
	    //TODO - Uncomment after result feed works
	    //temp remove - imp
		//TODO
	    pulseView.setVisibility(View.VISIBLE);
		Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        if (on) {
        	callAction(i);   
    		//pulse.setDuration(1000);
    		pulseView.startAnimation(pulse);	
        	
	    } else {
	    	stopService(i);
	    	pulseView.clearAnimation();
	    }	
	}
	
	
	public void callAction(Intent intent){
		StringBuilder logMessage = new StringBuilder("MainActivity Found USB Device");
		
		/*UsbDevice device = (UsbDevice) intent.
				getParcelableExtra(UsbManager.EXTRA_DEVICE);
		Log.d(TAG, "Found USB Device: "+device.toString());
		String logMessage = "Found USB Device";
		try{

			new LogToServerTask().execute("http://raostravelogue.com/roller-services/xmlrpc",logMessage);

		}catch(Exception e){
			Log.d(TAG, e.getLocalizedMessage());
		}*/
		//temp commenting below code
		
		manager = (UsbManager) getSystemService(Context.USB_SERVICE);
		HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		//int count = 0;
		while(deviceIterator.hasNext()){
			tmpdevice = deviceIterator.next();
			
			//Log.d(TAG, "Found USB Device inside loop: "+tmpdevice.toString());
			if (tmpdevice.getVendorId()==VENDOR_ID && tmpdevice.getProductId()==PRODUCT_ID
					){
				try{
						
					UsbEndpoint input = null;
					UsbInterface usbInterface = tmpdevice.getInterface(0);
					for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
						logMessage.append(" Endpoint Type : ").append(usbInterface.getEndpoint(i).getType());
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
			}
			
		}
		
		try{
		    logMessage.append( "MainActivity simple dummy");
			//new LogToServerTask().execute("http://raostravelogue.com/roller-services/xmlrpc",logMessage.toString());

		}catch(Exception e){
			Log.d(TAG, e.getLocalizedMessage());
			Log.d(TAG, e.getLocalizedMessage());
			//new LogToServerTask().execute("http://raostravelogue.com/roller-services/xmlrpc",e.getLocalizedMessage());
			
		}

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
			loadPrefs();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSwitchButtonClicked(boolean callService) {
		Intent i = new Intent(getApplicationContext(), AQMResultsFeedService.class);
		if(callService){
			startService(i);
			Toast.makeText(getApplicationContext(), "AQM Result Feed is turned on",Toast.LENGTH_SHORT).show();
		}else{
			stopService(i);
			Toast.makeText(getApplicationContext(), "AQM Result Feed is turned off",Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	public void onCreateGetImgView(View v) {
		// Say Thank you to Fragment
		pulseView = (ImageView)v;
	}
	
	
	//Async task
}
