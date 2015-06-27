package com.example.aspiraondroid.view.pieces;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.example.aspiraondroid.PreferenceSettings;
import com.example.aspiraondroid.view.PiecesManager;



/**
 * @author Sunil Rao
 *
 */
public class Fish extends Entity{
	private Paint paint;
	private boolean bounceUp = true;
	private float speed = 0.25f;
	private PiecesManager pieces;


	SharedPreferences prefs = null;
	private Bitmap image;


	private static final String TAG = "Fish";
	


	public Fish(float topLeftX, float topLeftY,float pivotY,PiecesManager manager,
			int color,Bitmap image){
		super(new Rect(),manager.getContext(),pivotY);	
		paint = new Paint();
		paint.setColor(color);
		this.setTopLeftX(topLeftX);
		this.setTopLeftY(topLeftY);
		prefs = getCtx().getSharedPreferences(PreferenceSettings.CONFIG_NAME, Context.MODE_PRIVATE);
		pieces = manager;
		this.image = image;
	}

	public void setPiecesManager(PiecesManager manager) {
		pieces = manager;
	}

	@Override
	public void draw(Canvas canvas) {
		try{
			canvas.save();
			canvas.drawBitmap(image, getTopLeftX(),getTopLeftY(), paint);
			//		canvas.drawCircle(pos.getX(), pos.getY(), 10, paint);
			//canvas.drawRect(getCollisionBox(), paint);
			canvas.restore();
		}catch (Exception e){
			Log.d(TAG, e.toString());
			StringBuilder error = new StringBuilder("");
			for (StackTraceElement element : e.getStackTrace()){
				error.append(element);
				error.append("\n");
			}

		}
	}

	private void move() {
		if(bounceUp){
			setTopLeftY(getTopLeftY() + speed);
		}else{
			setTopLeftY(getTopLeftY() - speed);
		}
	}

	public boolean performClick(){
		try{

			int stage = calculateStage();
			int teaseIndex = Integer.valueOf(prefs.getString("asthmaglobals.teaseIndex", "2"));
			String mood="sleepy";
			for (ImageSource img: ImageSource.values()){
				if (img.getIndex()==teaseIndex)
					mood=img.getMood();
			}
			
			for (Entity ent : pieces.getPieces()) {
				if (ent == this){
					String name = mood+"_" + stage;
					int resourceId = getCtx().getResources().getIdentifier(name, "drawable",getCtx().getPackageName());
					this.image = BitmapFactory.decodeResource(getCtx().getResources(), resourceId);
					((Fish)ent).setImage(image);
				}
			}
			teaseIndex = teaseIndex + 1;
			if (teaseIndex>2)
				teaseIndex = 0;
			Log.d(TAG,"Val of teaseIndex before update : "+teaseIndex);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putString("asthmaglobals.teaseIndex", String.valueOf(teaseIndex));
			edit.putString("asthmaglobals.teaseSnapTimeinMillis",String.valueOf(new Date().getTime()));
			edit.commit();
		}
		catch (Exception e){
			Log.d(TAG, e.toString());
			StringBuilder error = new StringBuilder("");
			for (StackTraceElement element : e.getStackTrace()){
				error.append(element);
				error.append("\n");
			}
		}
		return true;

	}
	

	public void changeMood(){
		String change2mood = prefs.getString("asthmaglobals.config.animation.changeMood2","");
		if (change2mood.length()>0){
			for (Entity ent : pieces.getPieces()) {
				if (ent == this){ 
					String name = change2mood+"_" + calculateStage();
					int resourceId = getCtx().getResources().getIdentifier(name, "drawable",getCtx().getPackageName());
					this.image = BitmapFactory.decodeResource(getCtx().getResources(), resourceId);
					((Fish)ent).setImage(image);
				}
			}
			SharedPreferences.Editor edit = prefs.edit();
			edit.putString("asthmaglobals.config.animation.changeMood2","");
			edit.commit();
		}
	}





	public int calculateStage() {
		Date today = new Date();
		long startDateMillis = Long.valueOf(prefs.getString("asthmaglobals.config.animation.startDateMilliSec", String.valueOf(Calendar.getInstance().getTimeInMillis())));
		Date startDate = new Date(startDateMillis);
		int daysElapsed = Math.abs(today.compareTo(startDate));
		int totaldays = Integer.valueOf(prefs.getString("asthmaglobals.config.animation.totalDays", "40"));
		int totalstages = Integer.valueOf(prefs.getString("asthmaglobals.config.animation.totalStages", "10"));
		int nextstagedaycount = Math.round(totaldays/totalstages);
		int whichStage = 0;
		for (int i = 0; i <= totaldays; i += nextstagedaycount) {
			if (daysElapsed <= i) {
				return whichStage ;
			}
			whichStage++;
		}

		return whichStage;
	}

	@Override
	public void processAI() {
		try{
			long teaseSnapTimeinMillis = -1;
			teaseSnapTimeinMillis=Long.valueOf(prefs.getString("asthmaglobals.teaseSnapTimeinMillis","0"));
			if (teaseSnapTimeinMillis>0){
				changeMood();
				if(new Date().getTime() - teaseSnapTimeinMillis > 10000){
					for (Entity ent : pieces.getPieces()) {
						if (ent == this){
							String mood = "sleepy";
							mood = prefs.getString("asthmaglobals.config.animation.currMood",mood);
							String name = mood+"_" + calculateStage();
							int resourceId = getCtx().getResources().getIdentifier(name, "drawable",getCtx().getPackageName());
							this.image = BitmapFactory.decodeResource(getCtx().getResources(), resourceId);
							((Fish)ent).setImage(image);
						}
					}
					SharedPreferences.Editor edit = prefs.edit();
					edit.putString("asthmaglobals.teaseSnapTimeinMillis","0");
					edit.commit();
				}
			}
			
			if (getTopLeftY() - getPivotY() > 50)
				bounceUp = false;
			else if( getPivotY() - getTopLeftY() > -5)
				bounceUp = true;
			move();
		}catch (Exception e){
			Log.d("Fish processAI", "Exception is : "+e.getLocalizedMessage());
		}
	}
	
	
	public SharedPreferences getPrefs() {
		return prefs;
	}
	
	public void setPrefs(SharedPreferences prefs) {
		this.prefs = prefs;
	}
	public PiecesManager getPieces() {
		return pieces;
	}

	public void setPieces(PiecesManager pieces) {
		this.pieces = pieces;
	}
	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}


}

enum ImageSource{
	ATTENTIVE(0,"attentive"),SCARED(1,"scared"),PUFFED(2,"puffed");
	private int index;
	private String mood;

	public String getMood() {
		return mood;
	}
	public void setMood(String mood) {
		this.mood = mood;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	ImageSource(int index,String mood){
		this.index = index;
		this.mood = mood;
	}


}


