package com.example.aspiraondroid.service;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.aspiraondroid.ManualSpirometerReading;
import com.example.aspiraondroid.PreferenceSettings;
import com.example.aspiraondroid.R;


public class SpirometerReadingService extends Service{

	private volatile ScheduledExecutorService scheduledThreadPool = null;
	private SharedPreferences prefs = null;
	private static final String TAG = "SpirometerReadingService";
	private static int grace_period = 200;
	private static int dynamicalertinterval = 300000;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void onCreate() {
		prefs = getApplicationContext().getSharedPreferences(PreferenceSettings.CONFIG_NAME, MODE_PRIVATE);

		determineNextReading();

		try{
			if(scheduledThreadPool!=null)
				scheduledThreadPool = null;
			long nextReadinginMiilis = Long.valueOf(prefs.getString("asthmaglobals.nextReadingTimeinMillis", "0"));
			long periodic_delay = -1;
			long time_now = new Date().getTime();
			periodic_delay = nextReadinginMiilis - time_now;
			//Toast.makeText(getApplicationContext(), "Periodic delay is : "+String.valueOf(periodic_delay), Toast.LENGTH_LONG).show();
			scheduledThreadPool = Executors.newScheduledThreadPool(2);	

			if (nextReadinginMiilis>0 && periodic_delay>0){
				//Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

				if(!scheduledThreadPool.isShutdown()){
					scheduledThreadPool.schedule(new Runnable() {
						public void run() {
							notifyUsers(false); 
						}
					}, periodic_delay,TimeUnit.MILLISECONDS);
				}
			}
			/*if(!scheduledThreadPool.isShutdown()){
				scheduledThreadPool.schedule(new Runnable() {
					public void run() {
						checkAndSetDynamicAlerts(); 
					}
				}, dynamicalertinterval,TimeUnit.MILLISECONDS);
			}*/

		}catch (Exception e){
			Log.d(TAG, "Exception in onCreate "+e.getLocalizedMessage());
		}


	}

	public void notifyUsers(boolean rightAway) {
		try{
			//Toast.makeText(getApplicationContext(), "inside notifyUsers", Toast.LENGTH_LONG).show();;
			Context context = getApplicationContext();
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_launcher)
			.setAutoCancel(true)
			.setContentTitle("AspiraOnDroid Notification");
			NotificationManager mNotificationManager =
					(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Intent notificationIntent = new Intent(context, ManualSpirometerReading.class);
			/*	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
					Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

			PendingIntent notifyPendingIntent =
					PendingIntent.getActivity(
							getApplicationContext(),
							0,
							notificationIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

			// Puts the PendingIntent into the notification builder
			builder.setContentIntent(notifyPendingIntent);

			// Notifications are issued by sending them to the
			// NotificationManager system service.

			// Builds an anonymous Notification object from the builder, and
			// passes it to the NotificationManager
			mNotificationManager.notify(0, builder.build());
			determineNextReading();
			long nextReadinginMiilis = Long.valueOf(prefs.getString("asthmaglobals.nextReadingTimeinMillis", "0"));
			long periodic_delay = -1;
			long time_now = new Date().getTime();
			periodic_delay = nextReadinginMiilis - time_now;
			//One second delay
			if (rightAway)
				periodic_delay = 1000;

			if (nextReadinginMiilis>0 && periodic_delay>0){
				if(!scheduledThreadPool.isShutdown()){
					scheduledThreadPool.schedule(new Runnable() {
						public void run() {
							notifyUsers(false);
							SharedPreferences.Editor editor = prefs.edit();
							editor.putString("asthmaglobals.teaseSnapTimeinMillis",String.valueOf(new Date().getTime()));
							editor.putString("asthmaglobals.config.animation.currMood", "attentive");
							editor.commit();
						}
					}, periodic_delay,TimeUnit.MILLISECONDS);
				}
			}
		}catch (Exception e){
			Log.d(TAG, "Exception in notifyUsers "+e.getLocalizedMessage());
		}

	}

	public void checkAndSetDynamicAlerts(){

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){	
		if (prefs == null)
			prefs = getApplicationContext().getSharedPreferences(PreferenceSettings.CONFIG_NAME, MODE_PRIVATE);
		return START_STICKY;
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
		String am_pm = cal.get(Calendar.HOUR_OF_DAY)>12?"PM":"AM";
		retValue = String.format("%02d",cal.get(Calendar.HOUR)) + ":"+ String.format("%02d", cal.get(Calendar.MINUTE)) 
				+ " " +  am_pm;
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("asthmaglobals.nextReadingTimeinMillis", String.valueOf(cal.getTimeInMillis()));
		editor.commit();
		return retValue;
	}





}
