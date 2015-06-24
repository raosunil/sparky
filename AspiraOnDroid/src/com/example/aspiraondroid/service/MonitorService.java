package com.example.aspiraondroid.service;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.aspiraondroid.DeviceHolder;
import com.example.aspiraondroid.PreferenceSettings;
import com.example.aspiraondroid.dao.DatabaseHandler;
import com.example.aspiraondroid.model.DylosReading;
import com.google.gson.Gson;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

public class MonitorService extends Service {
	//initializers only - actual values comes from properties file
		private int thread_size = 2;
		private int grace_period = 200;
		private volatile ScheduledExecutorService scheduledThreadPool = null;	
		private static final String TAG = "MonitorService";
		private DatabaseHandler dbHandler = null;
		private UsbManager manager = null;
		private UsbDevice tmpdevice = null;
		
		private static final Object lock = new Object();
		private static volatile boolean someoneDidTheHonor = false;
		
		private String deviceId = null;
		private String userId = null;
		private String geoLatitude = null;
		private String geoLongitude = null;
		private String geoMethod = null;
		private String uri = null;
		private long periodic_delay = -1;
		SharedPreferences prefs = null;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){	
		if (tmpdevice == null || manager == null){
			tmpdevice = DeviceHolder.getInstance().getDevice();
			manager = (UsbManager)getApplicationContext().getSystemService(Context.USB_SERVICE);
		}
		return START_STICKY;
	}
	
	@Override
	public void onCreate() {
		if(scheduledThreadPool!=null)
			scheduledThreadPool = null;

		Log.d("MonitorService", "Inside onCreate");
		try {
			//Database setup
			dbHandler = new DatabaseHandler(this); 
			tmpdevice = DeviceHolder.getInstance().getDevice();
			manager = (UsbManager)getApplicationContext().getSystemService(Context.USB_SERVICE);
			prefs = getApplicationContext().getSharedPreferences(PreferenceSettings.CONFIG_NAME, MODE_PRIVATE);
			thread_size = Integer.valueOf(prefs.getString("thread_size", "2"));
			grace_period = Integer.valueOf(prefs.getString("grace_period", "200"));
			periodic_delay = Long.parseLong(prefs.getString("periodic_delay", "300000"));
			deviceId = prefs.getString("deviceId", "aqm0");
			userId = prefs.getString("userId", "user0");
			geoMethod = prefs.getString("geoMethod", "manual");
			uri = prefs.getString("url", "http://lead2.poly.asu.edu:8090/AQMEcho/aqmecho");
			geoLongitude = prefs.getString("geoLatitude", "63.017578125");
			geoLatitude = prefs.getString("geoLongitude", "36.10743118848039");
				
			scheduledThreadPool = Executors.newScheduledThreadPool(thread_size);	
			if(!scheduledThreadPool.isShutdown()){
				scheduledThreadPool.scheduleWithFixedDelay(new UsbCommunciation(manager,tmpdevice), periodic_delay,periodic_delay, TimeUnit.MILLISECONDS);
			}


		} catch (RejectedExecutionException e){
			if(!scheduledThreadPool.isShutdown()){
				Log.d(TAG,"Task Submission Rejected",e);
				Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			}
			
		}catch (Exception e){
			Log.d(TAG,"Task Submission Rejected",e);
		}		  

	}
	
	

	@Override
	public void onDestroy(){
		if(!scheduledThreadPool.isShutdown()) {			
			try {
				scheduledThreadPool.shutdownNow();
				scheduledThreadPool.awaitTermination(grace_period, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Log.d(TAG,"Executor Service Termination exception",e);
			}finally{
				scheduledThreadPool = null;
			}
			if(dbHandler!=null){
				dbHandler.close();
			}
			super.onDestroy();
		}
	}

	private class UsbCommunciation implements Runnable {
		private static final int MAX_MESSAGE_SIZE = 32;
		private int timeOutMs = 10000;
		private UsbDevice device;
		private UsbManager mgr;
		UsbCommunciation(UsbManager manager, UsbDevice device) {
			this.device = device;
			this.mgr = manager;
		}

		@Override
		public void run() {
			UsbSerialDriver driver = UsbSerialProber.acquire(mgr,device);
			StringBuffer result = new StringBuffer(MAX_MESSAGE_SIZE);
			int numBytesRead = -1;
			boolean mStop = false;
			try{

				// Open a connection to the first available driver.
				// Get UsbManager from Android.
				
				if (driver != null) {
					
					synchronized(lock){
						while(someoneDidTheHonor){
							lock.wait();
						}
						someoneDidTheHonor = true;	
						try {
							
							driver.open();
							driver.setDTR(true);
							driver.setParameters(9600, 
									UsbSerialDriver.DATABITS_8, 
									UsbSerialDriver.STOPBITS_1, 
									UsbSerialDriver.PARITY_NONE);

							byte buffer[] = new byte[16];
							while(!mStop){
								numBytesRead = driver.read(buffer,0);
								if (numBytesRead==0)
									continue;
								String tmp = new String(buffer, 0, numBytesRead);
								//result.append(new String(new String(buffer, 0, numBytesRead)));
								result.append(tmp);
								if ((result.capacity() - result.length()) <8){
									mStop = true;
									buffer = null;
								}

							}
							
						} catch (IOException e) {
							// Deal with error.
						} finally {
							driver.close();
						} 
						someoneDidTheHonor = false;	
						lock.notifyAll();
					}
				}
				//TODO Persist in DB - DataStream

				//TODO JSON Streamify
				//new LogToServerWithinTask().execute("http://raostravelogue.com/roller-services/xmlrpc"," : MonitorService input string  : " + result.toString() );
				//end
				String[] lines = result.toString().split("\n");
				if (lines==null || lines.length<0){
					Toast.makeText(getApplicationContext(), "Error reading data from AQM", Toast.LENGTH_LONG).show();
					
				}
				StringBuffer yetanotherResult = new StringBuffer("");
				for (String line : lines){
					String[] recordData = line.split(",");
					if (recordData!=null && recordData.length==2){
						int smallParticle = Integer.parseInt(recordData[0].trim());
						int largeParticle = Integer.parseInt(recordData[1].trim());
						DylosReading dr = new DylosReading(deviceId, userId, new Date().getTime(), 
								smallParticle, largeParticle, Double.parseDouble(geoLatitude), Double.parseDouble(geoLongitude), geoMethod);
						String json =  new Gson().toJson(dr);
						dbHandler.Add_DylosReading(dr);
						yetanotherResult.append(json).append("\n");
						//start
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
						//end
						
					}
				}

			
			}catch(Exception e){
				Log.d(TAG, e.getLocalizedMessage());
				StringBuilder error = new StringBuilder("");
				for (StackTraceElement element : e.getStackTrace()){
					error.append(element);
					error.append("\n");
				}
				Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
				
			}
		}
	
	}
}
