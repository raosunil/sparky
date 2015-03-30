package com.example.dylosusbdatatransferapp.service;

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

import com.example.dylosusbdatatransferapp.DeviceHolder;
import com.example.dylosusbdatatransferapp.dao.DatabaseHandler;
import com.example.dylosusbdatatransferapp.model.DylosReading;
import com.google.gson.Gson;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

public class MonitorService extends Service {
	//initializers only - actual values comes from properties file
		private static int thread_size = 2;
		private static int grace_period = 200;
		private  long periodic_delay = 600000;
		private volatile ScheduledExecutorService scheduledThreadPool = null;	
		private static final String TAG = "MonitorService";
		private DatabaseHandler dbHandler = null;
		private UsbManager manager = null;
		private UsbDevice tmpdevice = null;
		
		private static final Object lock = new Object();
		private static boolean someoneDidTheHonor = false;
		
		private String deviceId = null;
		private String userId = null;
		private String geoLatitude = null;
		private String geoLongitude = null;
		private String geoMethod = null;
		private String uri = null;
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
			manager = (UsbManager)getApplicationContext().getSystemService(Context.USB_SERVICE);
			SharedPreferences prefs =
					PreferenceManager.getDefaultSharedPreferences(getBaseContext());

			thread_size = Integer.valueOf(prefs.getString("thread_size", "2"));
			grace_period = Integer.valueOf(prefs.getString("grace_period", "200"));
			periodic_delay = Long.parseLong(prefs.getString("periodic_delay", "120000"));
			deviceId = prefs.getString("deviceId", null);
			userId = prefs.getString("userId", null);
			geoMethod = prefs.getString("geoMethod", null);
			uri = prefs.getString("url", null);
			geoLongitude = prefs.getString("geoLongitude", null);
			geoLatitude = prefs.getString("geoLatitude", null);


			if (tmpdevice == null)
				tmpdevice = DeviceHolder.getInstance().getDevice();
				

			//new LogToServerTask().execute("http://raostravelogue.com/roller-services/xmlrpc", " : MonitorService inside onCreate " + tmpdevice.toString() + " :device string");
			//new LogToServerTask().execute("http://raostravelogue.com/roller-services/xmlrpc", " : MonitorService inside onCreate " + thread_size +" : "+ periodic_delay + " : "+grace_period + " : "+geoMethod + " :shared pref string");
			//new LogToServerTask().execute("http://raostravelogue.com/roller-services/xmlrpc", " : MonitorService inside onCreate " + manager.toString() + " :manager string");
			
			//UsbSerialDriver driver = UsbSerialProber.acquire(manager,tmpdevice);

			scheduledThreadPool = Executors.newScheduledThreadPool(thread_size);	
			if(!scheduledThreadPool.isShutdown()){
				scheduledThreadPool.scheduleWithFixedDelay(new UsbCommunciation(manager,tmpdevice), periodic_delay,periodic_delay, TimeUnit.MILLISECONDS);
			}


		} catch (RejectedExecutionException e){
			if(!scheduledThreadPool.isShutdown()){
				Log.d(TAG,"Task Submission Rejected",e);
				//new LogToServerTask().execute("http://raostravelogue.com/roller-services/xmlrpc", " : MonitorService inside onCreate exception" + e.getLocalizedMessage());
			}
			//new LogToServerTask().execute("http://raostravelogue.com/roller-services/xmlrpc", " : MonitorService inside onCreate exception" + e.getLocalizedMessage());
		}catch (Exception e){
			Log.d(TAG,"Task Submission Rejected",e);
			//new LogToServerTask().execute("http://raostravelogue.com/roller-services/xmlrpc", " : MonitorService inside onCreate exception" + e.getLocalizedMessage());
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
			//new LogToServerTask().execute("http://raostravelogue.com/roller-services/xmlrpc", " : MonitorService called destroy : " );
			super.onDestroy();
		}
	}
	


	private class UsbCommunciation implements Runnable {
		//private final UsbManager manager = (UsbManager)getApplicationContext().getSystemService(Context.USB_SERVICE);
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
								if ((result.capacity() - result.length()) <=8){
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

				//new LogToServerWithinTask().execute("http://raostravelogue.com/roller-services/xmlrpc",yetanotherResult.toString() + " : MonitorService JSON strings : count : " + dbHandler.Get_DylosReadings().size());
				//new LogToServerWithinTask().execute("http://raostravelogue.com/roller-services/xmlrpc",result.toString() + " : MonitorService bytes read : " + numBytesRead);

			}catch(Exception e){
				Log.d(TAG, e.getLocalizedMessage());
				StringBuilder error = new StringBuilder("");
				for (StackTraceElement element : e.getStackTrace()){
					error.append(element);
					error.append("\n");
				}
				//new LogToServerWithinTask().execute("http://raostravelogue.com/roller-services/xmlrpc",error.toString());

			}
		}
	//Async task
	}
	
	//Async task
}
