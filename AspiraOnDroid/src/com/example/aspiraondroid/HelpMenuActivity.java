package com.example.aspiraondroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import android.view.MotionEvent;

public class HelpMenuActivity extends Activity{

	//private MediaPlayer mediaPlayer = null;
	private static final String TAG = "HelpMenuActivity";
	private VideoView videoHolder ;
	private TextView contactName;
	private TextView contactPhone;
	private ImageView img;
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		videoHolder  = new VideoView(this);
		img = new ImageView(this);
		setContentView(R.layout.help_layout);
		contactName = (TextView)findViewById(R.id.textContactName);
		contactPhone = (TextView)findViewById(R.id.textViewContactPhone);
		prefs = getApplicationContext().getSharedPreferences(PreferenceSettings.CONFIG_NAME, MODE_PRIVATE);
		contactName.setText(prefs.getString("asthmaglobals.config.contactName", "abef"));
		contactPhone.setText(prefs.getString("asthmaglobals.config.contactPhone", "(123)456-789"));


		/*mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer player) {
				player.start();
			}

		});*/
	}

	public void executeImgBtn1Click(View v){
		try{
			Uri video = Uri.parse("android.resource://" + getPackageName() + "/"+R.raw.step1);
			/*mediaPlayer.setDataSource(getApplicationContext(),video);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.prepareAsync();*/
			getWindow().setFormat(PixelFormat.TRANSLUCENT);
			videoHolder.setMediaController(new MediaController(this));
			videoHolder.setVideoURI(video);
			setContentView(videoHolder);
			videoHolder.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					((VideoView)v).stopPlayback();
					jumpMain();
					return true;
				}
			});
			videoHolder.start();
		}catch (Exception e){
			Log.d(TAG, e.getLocalizedMessage());
		}

	}

	public void executeImgBtn2Click(View v){
		try{
			Uri video = Uri.parse("android.resource://" + getPackageName() + "/"+R.raw.step2);
			/*mediaPlayer.setDataSource(getApplicationContext(),video);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.prepareAsync();*/
			getWindow().setFormat(PixelFormat.TRANSLUCENT);
			videoHolder.setMediaController(new MediaController(this));
			videoHolder.setVideoURI(video);
			setContentView(videoHolder);
			videoHolder.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					((VideoView)v).stopPlayback();
					jumpMain();
					return true;
				}
			});
			videoHolder.start();
		}catch (Exception e){
			Log.d(TAG, e.getLocalizedMessage());
		}

	}

	public void executeImgBtn3Click(View v){
		try{
			Uri video = Uri.parse("android.resource://" + getPackageName() + "/"+R.raw.step3);
			/*mediaPlayer.setDataSource(getApplicationContext(),video);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.prepareAsync();*/
			getWindow().setFormat(PixelFormat.TRANSLUCENT);
			videoHolder.setMediaController(new MediaController(this));
			videoHolder.setVideoURI(video);
			setContentView(videoHolder);
			videoHolder.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					((VideoView)v).stopPlayback();
					jumpMain();
					return true;
				}
			});
			videoHolder.start();
		}catch (Exception e){
			Log.d(TAG, e.getLocalizedMessage());
		}
	}
	public void executeImgBtn4Click(View v){
		try{
			Uri imageURL = Uri.parse("android.resource://" + getPackageName() + "/"+R.raw.second);
			img.setImageURI(imageURL);
			setContentView(img);
			img.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					img.clearFocus();
					img.destroyDrawingCache();
					jumpMain();
					return true;
				}
			});
			
		}catch (Exception e){
			Log.d(TAG, e.getLocalizedMessage());
		}
	}

	public void executeImgBtn5Click(View v){
		try{
			Uri imageURL = Uri.parse("android.resource://" + getPackageName() + "/"+R.raw.second);
			img.setImageURI(imageURL);
			setContentView(img);
			img.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					img.clearFocus();
					img.destroyDrawingCache();
					jumpMain();
					return true;
				}
			});
			
		}catch (Exception e){
			Log.d(TAG, e.getLocalizedMessage());
		}

	}
	private synchronized void jumpMain() {
/*		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);*/
		super.onBackPressed();  
		//finish();
	}


	@Override
	protected void onStop(){
		super.onDestroy();
		if (videoHolder!=null){
			videoHolder = null;
		}
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		if (videoHolder!=null){
			videoHolder = null;
		}
	}


}
