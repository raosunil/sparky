package com.example.aspiraondroid.view;



import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.aspiraondroid.MainActivity;
import com.example.aspiraondroid.R;
import com.example.aspiraondroid.view.pieces.Entity;
import com.example.aspiraondroid.view.pieces.Fish;




/**
 * @author Sunil Rao
 *
 */
public class ComplexDynamicView extends View implements Runnable {
	//initial values only - gets real values after drawn
	private int width = 400, height = 700;
	private Handler handler;
	public static final int JAR_LABEL=1000;
	public static final int FISH_LABEL=1001;

	private PiecesManager manager;
	private Context ctx;	


	//used during createBall and also used during reset
	private boolean callOnce = false;


	public ComplexDynamicView(Context context) {
		super(context);
		this.ctx = context;
		init();
	}

	public ComplexDynamicView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctx = context;
		init();
	}

	public ComplexDynamicView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.ctx = context;
		init();
	}

	private void init() {
		/*background = new Paint();
		background.setColor(Color.WHITE);*/
		manager = new PiecesManager(ctx);
		Log.d("Fish jar pivot"," is "+getWidth()+" : " + getPivotY());
		Fish fish = new Fish(getWidth()/7 ,getPivotY(),getPivotY(),manager, Color.RED,
						BitmapFactory.decodeResource(getResources(), R.drawable.sleepy_1));
		manager.addPiece(fish);
	}

	public void onDraw(Canvas canvas) {		
		/*the below code snippet gets called after having got 
		the real width and height. Accordingly the canon ball is drawn after this step
		*/ 
		if (!callOnce){
			width = getWidth();
			height = getHeight();
			init();
			callOnce = true;
		}
		canvas.save();
		//canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		Drawable d = getResources().getDrawable(R.drawable.fishbowl);
		d.setBounds(0, 0, getWidth(), getHeight());		
		d.draw(canvas);
		manager.draw(canvas);
		canvas.restore();
	}



	@Override
	public void run() {		
		while (true) {
			try {			
				manager.processAI();
				Message msg = new Message();
				msg.what = MainActivity.REFRESH;
				handler.sendMessage(msg);
				Thread.sleep(10);
			} catch (Exception e) {
				Log.d("ComplexGameView run", e.getLocalizedMessage());
			}
		}
	}

	public void setCallbackHandler(Handler guiRefresher) {
		this.handler = guiRefresher;
	}




	public boolean isCallOnce() {
		return callOnce;
	}

	public void setCallOnce(boolean callOnce) {
		this.callOnce = callOnce;
	}
	
	public boolean onTouchEvent(MotionEvent evt) {
		if (evt.getActionMasked() == MotionEvent.ACTION_DOWN){
			for(Entity piece : manager.getPieces()){
				piece.performClick();
			}
		}
		return true;
		
	}
	


}