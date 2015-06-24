package com.example.aspiraondroid.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.aspiraondroid.model.SpirometerReading;



/**
 * @author Sunil
 *DAO class 
 */
public class SpirometerDBHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version 
	private static final int DATABASE_VERSION = DatabaseHandler.DATABASE_VERSION;
	private static final String TAG="SpirometerDBHandler";
	// Database Name
	private static final String DATABASE_NAME = DatabaseHandler.DATABASE_NAME;

	// SpirometerReading table name
	private static final String TABLE_GENERIC = "spirometerreading";

	/*
 table name - spirometerreading

fields

1) deviceid Text
2) patientid Text
3) readingtime Timestamp
4) measureid Int
5) manual boolean char(1)
6) pefvalue int
7) fev1value float
8) error int
9) bestvalue int
10) symptoms char(1)
11) groupid int
	 */


	private static final String KEY_ID = "SP_ID";
	private static final String KEY_deviceid = "deviceid";
	private static final String KEY_patientid = "patientid";
	private static final String KEY_readingtime = "readingtime";
	private static final String KEY_measureid = "measureid";
	private static final String KEY_is_manual= "manual";
	private static final String KEY_pefvalue = "pefvalue";
	private static final String KEY_fev1value = "fev1value";
	private static final String KEY_error = "error";
	private static final String KEY_bestvalue = "bestvalue";
	private static final String KEY_is_symptoms = "symptoms";
	private static final String KEY_groupid = "groupid";


	private final ArrayList<SpirometerReading> spmresults_list = new ArrayList<SpirometerReading>();

	public SpirometerDBHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG,"Inside onCreate ");
		String CREATE_SPMRESULTS_TABLE = "CREATE TABLE " + TABLE_GENERIC + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_deviceid + " TEXT,"
				+ KEY_patientid  + " TEXT ," + KEY_readingtime + " TEXT ,"
				+ KEY_measureid  + " INTEGER," +  KEY_is_manual  + " TEXT," 
				+   KEY_pefvalue  + " INTEGER," +   KEY_fev1value  + " REAL," 
				+ KEY_error  + " INTEGER ," + KEY_bestvalue + " INTEGER ," + KEY_is_symptoms + " TEXT ," 
				+ KEY_groupid + " INTEGER"
				+ ")";
		db.execSQL(CREATE_SPMRESULTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENERIC);

		// Create tables again
		onCreate(db);
	}


	public String getDateTimeAsString(Date t){
		String retValue = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			retValue = sdf.format(t);
			Log.d(TAG,"Date input is "+retValue);
		} catch (Exception e) {
			Log.e(TAG,"getDateTimeAsString Exception" +e.getLocalizedMessage());
		}
		return retValue;
	}

	public Date parseString2Date(String s){
		Date t = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			t = sdf.parse(s);
			Log.d(TAG,"Date output is "+t.toGMTString());

		} catch (Exception e) {
			Log.e(TAG,"parseString2TimeStamp Exception" +e.getLocalizedMessage());
		}
		return t;
	}

	// Adding new SpirometerReading
	public void Add_SpmResults(SpirometerReading sp) {

		try{
			Log.d(TAG,"Inside Add_SpmResults " );

			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(KEY_deviceid, sp.getDeviceId()); 
			values.put(KEY_patientid, sp.getPatientId()); 
			values.put(KEY_readingtime,getDateTimeAsString(sp.getMeasureDate())); 
			values.put(KEY_measureid, sp.getMeasureID()); 
			values.put(KEY_is_manual, sp.getManual()?"Y":"N"); 
			values.put(KEY_pefvalue, sp.getPEFValue()); 
			values.put(KEY_fev1value, sp.getFEV1Value()); 
			values.put(KEY_error, sp.getError()); 
			values.put(KEY_bestvalue, sp.getBestValue()); 
			values.put(KEY_is_symptoms, sp.getHasSymptoms()?"Y":"N"); 
			values.put(KEY_groupid, sp.getGroupId()); 

			// Inserting Row
			db.insert(TABLE_GENERIC, null, values);
			db.close(); // Closing database connection
		}catch(Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.d(TAG,sw.toString());
		}
	}


	// Getting single SPMResults
	SpirometerReading Get_SPMResults(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_GENERIC, new String[] { KEY_ID,
				KEY_deviceid,KEY_patientid, KEY_readingtime,KEY_measureid,KEY_is_manual,KEY_pefvalue,KEY_fev1value,KEY_error,KEY_bestvalue,KEY_is_symptoms,KEY_groupid}, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		SpirometerReading sp = new SpirometerReading(cursor.getString(1),
				cursor.getString(2), parseString2Date(cursor.getString(3)), 
				cursor.getInt(4), cursor.getString(5).trim().equalsIgnoreCase("Y")?true:false, 
						cursor.getInt(6), cursor.getFloat(7), cursor.getInt(8), cursor.getInt(9), 
						cursor.getString(10).trim().equalsIgnoreCase("Y")?true:false, cursor.getInt(11));

		cursor.close();
		db.close();

		return sp;

	}

	// Getting All SPMResults
	public ArrayList<SpirometerReading> Get_SPMResults() {
		try {
			spmresults_list.clear();

			// Select All Query
			String selectQuery = "SELECT  " + KEY_ID + ", " 
					+ KEY_deviceid + ", " + KEY_patientid + ", " + KEY_readingtime+ ", " +KEY_measureid+ ", " +KEY_is_manual+ ", " 
					+KEY_pefvalue+ ", " +KEY_fev1value+ ", " +KEY_error+ ", " 
					+KEY_bestvalue+ ", " +KEY_is_symptoms+ ", " +KEY_groupid
					+  " FROM " + TABLE_GENERIC + " ORDER BY "+ KEY_ID+"  DESC";

			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					SpirometerReading sp = new SpirometerReading(cursor.getString(1),
							cursor.getString(2), parseString2Date(cursor.getString(3)), 
							cursor.getInt(4), cursor.getString(5).trim().equalsIgnoreCase("Y")?true:false, 
									cursor.getInt(6), cursor.getFloat(7), cursor.getInt(8), cursor.getInt(9), 
									cursor.getString(10).trim().equalsIgnoreCase("Y")?true:false, cursor.getInt(11));
					// Adding entry to list
					spmresults_list.add(sp);
				} while (cursor.moveToNext());
			}

			// return entry list
			cursor.close();
			db.close();	    
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("all_spmresults", "" + e);
		}

		return spmresults_list;
	}



	// Deleting single entry
	public void Delete_SPMResults(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_GENERIC, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
		db.close();
	}

	public void Delete_All_SPMResults() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_GENERIC, KEY_ID + " > ?",
				new String[] { String.valueOf(0) });
		db.close();
	}

	// Getting SpirometerReadings Count
	public int Get_Total_SpmResults() {
		int retValue = -1;   
		String countQuery = "SELECT  * FROM " + TABLE_GENERIC;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		retValue = cursor.getCount();
		cursor.close();	
		db.close();
		// return count
		return retValue;
	}

	// Getting max SpirometerReading id
	public int Get_Max_AqmResults() {
		int retValue = -1;   
		String countQuery = "SELECT  max(" + KEY_ID + ") FROM " + TABLE_GENERIC;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		if (cursor.moveToFirst()) {
			retValue = cursor.getInt(0);
		}
		cursor.close();	
		db.close();
		// return count
		return retValue;
	}



}
