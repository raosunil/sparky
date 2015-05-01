package com.example.dylosusbdatatransferapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.dylosusbdatatransferapp.R;
import com.example.dylosusbdatatransferapp.dao.AQMResultsDBHandler;
import com.example.dylosusbdatatransferapp.model.AQMResults;
import com.example.dylosusbdatatransferapp.notification.LoopBackEventHandlerActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AQMResultsFeedService extends Service {
	//initializers only - actual values comes from properties file
		private static int thread_size = 1;
		private static int grace_period = 200;
		private  long periodic_delay = 60;
		private String URL = "";
		private int numGroupsWithin1WayANOVA;
		private volatile ScheduledExecutorService scheduledThreadPool = null;	
		private static final String TAG = "AQMResultsFeedService";
		private AQMResultsDBHandler dbHandler = null;
		
		public static final String PUT_EXTRA_IS_GLOBALLY_BAD_BOOL = "isGloballBad";
		public static final String PUT_EXTRA_IS_GLOBALLY_GOOD_BOOL = "isGloballGood";
		public static final String PUT_EXTRA_DEVICE_INDIV_BADNESS_BOOLARR = "locallyBadDeviceArray";
		public static final String PUT_EXTRA_DEVICE_NAMES_STRARR = "deviceNames";
		public static final String PUT_EXTRA_PREDICTION_VALUES = "predictedValues";
		public static final String PUT_EXTRA_TIME_AS_STRING = "timeasString";
		
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){	
		return START_STICKY;
	}
	
	@Override
	public void onCreate() {
		if(scheduledThreadPool!=null)
			scheduledThreadPool = null;

		Log.d(TAG, "Inside onCreate");
		try {
			//Database setup
			dbHandler = new AQMResultsDBHandler(this); 
			SharedPreferences prefs =
					PreferenceManager.getDefaultSharedPreferences(getBaseContext());

			URL = String.valueOf(prefs.getString("ResultFeed_URL", "http://raostravelogue.com/getJSONAQMResult"));
			numGroupsWithin1WayANOVA = Integer.valueOf(prefs.getString("numGroupsWithin1WayANOVA","2"));
			periodic_delay = Integer.valueOf(prefs.getString("ResultFeed_Interval_Minutes", "4"));
			Log.d(TAG,"Value of periodic_delay is " + periodic_delay);
			grace_period = Integer.valueOf(prefs.getString("grace_period", "200"));
			scheduledThreadPool = Executors.newScheduledThreadPool(thread_size);	
			//TODO - Call a thead to insert rows into DB
			if(!scheduledThreadPool.isShutdown()){
				scheduledThreadPool.scheduleWithFixedDelay(new ResultFeeder(), periodic_delay,periodic_delay, TimeUnit.MINUTES);
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
	


	private class ResultFeeder implements Runnable {
		//private final UsbManager manager = (UsbManager)getApplicationContext().getSystemService(Context.USB_SERVICE);
		
		@Override
		public void run() {
			BufferedReader bufferedReader = null;
			try{
				//TODO Persist in DB - DataStream

				//TODO JSON Streamify
				StringBuffer stringBuffer = new StringBuffer(100);
				
				boolean result = false;
				String body = null;
				char[] charBuffer = new char[128];
				int bytesRead = -1;


				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet();
				request.setHeader("Content-Type", "text/css; charset=utf-8");
				request.setURI(new URI(URL));
				HttpResponse response = client.execute(request);
				bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuffer.append(charBuffer, 0, bytesRead);
				}
				body = stringBuffer.toString();
				if (body != null && body.length()>0) {
					int beginIndex = body.indexOf("[");
					int lastIndex = body.lastIndexOf("]");
					String actualBody = body.substring(beginIndex, lastIndex+1);
					actualBody = actualBody.replaceAll("T", " ");
					GsonBuilder gsonBuilder = new GsonBuilder();
					   
				    gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
				    
				    Gson gson = gsonBuilder.create();
				    AQMResults[] listOfAqmResults = gson.fromJson(actualBody, AQMResults[].class);
					if (listOfAqmResults!=null && listOfAqmResults.length>0){
						Log.d(TAG, "Obtained JSON List"+listOfAqmResults.length);
						Log.d(TAG,"Printing the first object: "+listOfAqmResults[1].getClass());
						for(AQMResults aqmresult: listOfAqmResults){
							String badnessIndicator = aqmresult.getBadness_indicators().substring(1);
							boolean status = false;
							boolean globalBadStatus = true;
							boolean golbalGoodStatus = true;
							String[] mean_Array = aqmresult.getMean_values().substring(1).split("\\|");
							int n_r = mean_Array.length;
							String devices[] = aqmresult.getComparison_type().substring(1).split("\\|");
							int n_b = devices.length;
							boolean[] deviceIndividialBadness = new boolean[n_b];
							int n_w = n_r/n_b; 
							
							for(String str: badnessIndicator.split("\\|")){
								globalBadStatus = globalBadStatus && Boolean.valueOf(str);
								golbalGoodStatus = golbalGoodStatus && !Boolean.valueOf(str);
							}
							String[] badnessArray = badnessIndicator.split("\\|");
							if (!globalBadStatus && !golbalGoodStatus){
								int count = 0;
								for (int i = 0; i< n_b; i++){
									status = false;
									for (int j=0; j<n_w;j++){
										status = status || Boolean.valueOf(badnessArray[count]);
										count++;
									}
								if(status)
									deviceIndividialBadness[i] = status;
								}
							}
							
							
							if (!golbalGoodStatus){
								
								// Instantiate a Builder object.
								NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
										.setSmallIcon(R.drawable.ic_launcher)
								        .setContentTitle("DylosUSBReader Notification")
								        .setContentText("Action Needed");
								// Creates an Intent for the Activity
								Intent notifyIntent =
								        new Intent(getApplicationContext(),LoopBackEventHandlerActivity.class);
								// Sets the Activity to start in a new, empty task
								//notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
								       // Intent.FLAG_ACTIVITY_CLEAR_TASK);
								notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
								
								notifyIntent.putExtra(PUT_EXTRA_IS_GLOBALLY_BAD_BOOL, globalBadStatus);
								notifyIntent.putExtra(PUT_EXTRA_DEVICE_INDIV_BADNESS_BOOLARR, deviceIndividialBadness);
								notifyIntent.putExtra(PUT_EXTRA_DEVICE_NAMES_STRARR, devices);
								notifyIntent.putExtra(PUT_EXTRA_TIME_AS_STRING,new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(aqmresult.getAnalysis_starttime()));
								notifyIntent.putExtra(PUT_EXTRA_PREDICTION_VALUES, aqmresult.getPredicted_values());
								
								// Creates the PendingIntent
								PendingIntent notifyPendingIntent =
								        PendingIntent.getActivity(
								        getApplicationContext(),
								        0,
								        notifyIntent,
								        PendingIntent.FLAG_UPDATE_CURRENT
								);
								// Puts the PendingIntent into the notification builder
								builder.setContentIntent(notifyPendingIntent);
								// Notifications are issued by sending them to the
								// NotificationManager system service.
								NotificationManager mNotificationManager =
								    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
								// Builds an anonymous Notification object from the builder, and
								// passes it to the NotificationManager
								mNotificationManager.notify(0, builder.build());
							}
							dbHandler.Add_AqmResults(aqmresult);
							
							
						}
					}
					
				} else {
	    			new RuntimeException("No response found");
	    		}

	    	} catch (Throwable t1) {
	    		Log.d(TAG,"No response of JSON Feed: " + t1.getMessage());
	    	} finally {
	    		if (bufferedReader != null) {
	    			try {
	    				bufferedReader.close();
	    			} catch (IOException ex) {
	    				Log.d(TAG, "Exception closing the bufferedReader " + ex.getLocalizedMessage());
	    			}
	    		}
	    	}
		}
	}
				
}
