package com.example.dylosusbdatatransferapp.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.dylosusbdatatransferapp.model.AQMResults;

/**
 * @author Sunil
 *DAO class 
 */
public class AQMResultsDBHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version 
    private static final int DATABASE_VERSION = 6;
    private static final String TAG="AQMResultsDBHandler";
    // Database Name
    private static final String DATABASE_NAME = "dylosManager";

    // Dylos table name
    private static final String TABLE_GENERIC = "aqm_data_results";
    
    /*
  ----------------------------+--------------+------+-----+---------------------+-----------------------------+
| Field                      | Type         | Null | Key | Default             | Extra                       |
+----------------------------+--------------+------+-----+---------------------+-----------------------------+
| RES_ID                     | int(11)      | NO   | PRI | NULL                | auto_increment              |
| comparison_type            | varchar(64)  | NO   |     | NULL                |                             |
| analysis_starttime         | timestamp    | NO   |     | CURRENT_TIMESTAMP   | on update CURRENT_TIMESTAMP |
| analysis_endtime           | timestamp    | NO   |     | 0000-00-00 00:00:00 |                             |
| is_btw_grp_significant     | char(1)      | YES  |     | NULL                |                             |
| is_within_grp_significant  | char(1)      | YES  |     | NULL                |                             |
| is_interaction_significant | char(1)      | YES  |     | NULL                |                             |
| is_lineartrend_significant | char(1)      | YES  |     | NULL                |                             |
| badness_indicators         | varchar(200) | NO   |     | NULL                |                             |
| mean_values                | varchar(200) | NO   |     | NULL                |                             |
| predicted_values           | varchar(200) | NO   |     | NULL                |                             |
| modified_timestamp         | timestamp    | NO   |     | CURRENT_TIMESTAMP   | on update CURRENT_TIMESTAMP |
+----------------------------+--------------+------+-----+---------------------+-----------------------------+
12 rows in set (0.11 sec)

     */

  
    private static final String KEY_ID = "RES_ID";
    private static final String KEY_comparison_type = "comparison_type";
    private static final String KEY_analysis_starttime = "analysis_starttime";
    private static final String KEY_analysis_endtime = "analysis_endtime";
    private static final String KEY_is_btw_grp_significant = "is_btw_grp_significant";
    private static final String KEY_is_within_grp_significant= "is_within_grp_significant";
    private static final String KEY_is_interaction_significant = "is_interaction_significant";
    private static final String KEY_is_lineartrend_significant = "is_lineartrend_significant";
    private static final String KEY_badness_indicators = "badness_indicators";
    private static final String KEY_mean_values = "mean_values";
    private static final String KEY_predicted_values = "predicted_values";
    private static final String KEY_modified_timestamp = "modified_timestamp";
    private static final int MAX_ENTRIES = 2000;
   
    private final ArrayList<AQMResults> aqmresults_list = new ArrayList<AQMResults>();

    public AQMResultsDBHandler(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	Log.d(TAG,"Inside onCreate ");
	String CREATE_AQMRESULTS_TABLE = "CREATE TABLE " + TABLE_GENERIC + "("
		+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_comparison_type + " TEXT,"
		+ KEY_analysis_starttime  + " TEXT NOT NULL," + KEY_analysis_endtime + " TEXT NOT NULL,"
		+ KEY_is_btw_grp_significant  + " TEXT," +  KEY_is_within_grp_significant  + " TEXT," 
		+   KEY_is_interaction_significant  + " TEXT," +   KEY_is_lineartrend_significant  + " TEXT," 
		+ KEY_badness_indicators  + " TEXT NOT NULL," + KEY_mean_values + " TEXT NOT NULL," + KEY_predicted_values + " TEXT NOT NULL,"
		+ KEY_modified_timestamp + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL"
		+ ")";
	db.execSQL(CREATE_AQMRESULTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	// Drop older table if existed
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENERIC);

	// Create tables again
	onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    
    
    public String getDateTimeAsString(Timestamp t){
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
    
    public Timestamp parseString2TimeStamp(String s){
    	Timestamp t = null;
    	try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date d = sdf.parse(s);
			Log.d(TAG,"Date output is "+d.toGMTString());
			t = new Timestamp(d.getTime());
			
		} catch (Exception e) {
			Log.e(TAG,"parseString2TimeStamp Exception" +e.getLocalizedMessage());
		}
    	return t;
    }

    // Adding new DylosFeed
    public void Add_AqmResults(AQMResults aq) {
    	Timestamp max_end_time = Get_Max_AqmAnalysisEndTime();
    	int total_count = Get_Total_AqmResults();
    	if (total_count>1 && aq.getAnalysis_starttime().before(max_end_time) ){
    		//Do nothing
    		Log.d(TAG,"Inside do nothing block");
    	}
    	else{
    		try{
    			Log.d(TAG,"Inside Add_DylosReading : printing aq time and max end time "+aq.getAnalysis_starttime().getTime() + " : "+Get_Max_AqmAnalysisEndTime() );

    			SQLiteDatabase db = this.getWritableDatabase();

    			ContentValues values = new ContentValues();
    			values.put(KEY_comparison_type, (String)aq.getComparison_type()); 
    			values.put(KEY_analysis_starttime, getDateTimeAsString(aq.getAnalysis_starttime())); 
    			values.put(KEY_analysis_endtime, getDateTimeAsString(aq.getAnalysis_endtime())); 
    			values.put(KEY_is_btw_grp_significant, aq.isIs_btw_grp_significant()?"Y":"N"); 
    			values.put(KEY_is_within_grp_significant, aq.isIs_within_grp_significant()?"Y":"N"); 
    			values.put(KEY_is_interaction_significant, aq.isIs_interaction_significant()?"Y":"N"); 
    			values.put(KEY_is_lineartrend_significant, aq.isIs_lineartrend_significant()?"Y":"N"); 
    			values.put(KEY_badness_indicators, (String) aq.getBadness_indicators()); 
    			values.put(KEY_mean_values, (String) aq.getMean_values()); 
    			values.put(KEY_predicted_values, (String) aq.getPredicted_values()); 

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
    }

    // Getting single AQMResults
    AQMResults Get_AQMResults(int id) {
    	SQLiteDatabase db = this.getReadableDatabase();

    	Cursor cursor = db.query(TABLE_GENERIC, new String[] { KEY_ID,
    			KEY_comparison_type,KEY_analysis_starttime, KEY_analysis_endtime,KEY_is_btw_grp_significant,KEY_is_within_grp_significant,KEY_is_interaction_significant,KEY_is_lineartrend_significant,KEY_badness_indicators,KEY_mean_values,KEY_predicted_values,KEY_modified_timestamp}, KEY_ID + "=?",
    			new String[] { String.valueOf(id) }, null, null, null);
    	if (cursor != null)
    		cursor.moveToFirst();

    	AQMResults aq = new AQMResults();

    	aq.setComparison_type(cursor.getString(1));
    	Log.d(TAG, "Value of integer stored in DB"   +cursor.getInt(2) + " and "+cursor.getInt(3));
    	aq.setAnalysis_starttime(parseString2TimeStamp(cursor.getString(2)));
    	aq.setAnalysis_endtime(parseString2TimeStamp(cursor.getString(3)));
    	aq.setIs_btw_grp_significant(cursor.getString(4).trim().equalsIgnoreCase("Y")?true:false);
    	aq.setIs_within_grp_significant(cursor.getString(5).trim().equalsIgnoreCase("Y")?true:false);
    	aq.setIs_interaction_significant(cursor.getString(6).trim().equalsIgnoreCase("Y")?true:false);
    	aq.setIs_lineartrend_significant(cursor.getString(7).trim().equalsIgnoreCase("Y")?true:false);

    	aq.setBadness_indicators(cursor.getString(8));
    	aq.setMean_values(cursor.getString(9));
    	aq.setPredicted_values(cursor.getString(10));

    	cursor.close();
    	db.close();

    	return aq;
    	
    }

    // Getting All AQMResults
    public ArrayList<AQMResults> Get_AQMResults() {
	try {
		aqmresults_list.clear();

	    // Select All Query
	    String selectQuery = "SELECT  " + KEY_ID + "," + KEY_comparison_type + "," + KEY_analysis_starttime
	    				+ "," + KEY_analysis_endtime + "," + KEY_is_btw_grp_significant + "," + KEY_is_within_grp_significant
	    				+ "," + KEY_is_interaction_significant + "," + KEY_is_lineartrend_significant+ "," + KEY_badness_indicators
	    				+ "," + KEY_mean_values + "," + KEY_predicted_values+ "," + KEY_modified_timestamp 
	    				+  " FROM " + TABLE_GENERIC + " ORDER BY "+ KEY_analysis_starttime+"  DESC";

	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);

	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
		do {
			AQMResults aq = new AQMResults();
			aq.setComparison_type(cursor.getString(1));
			Log.d(TAG, "Value of integer stored in DB"   +cursor.getInt(2) + " and "+cursor.getInt(3));
	    	aq.setAnalysis_starttime(parseString2TimeStamp(cursor.getString(2)));
	    	aq.setAnalysis_endtime(parseString2TimeStamp(cursor.getString(3)));
	    	aq.setIs_btw_grp_significant(cursor.getString(4).trim().equalsIgnoreCase("Y")?true:false);
	    	aq.setIs_within_grp_significant(cursor.getString(5).trim().equalsIgnoreCase("Y")?true:false);
	    	aq.setIs_interaction_significant(cursor.getString(6).trim().equalsIgnoreCase("Y")?true:false);
	    	aq.setIs_lineartrend_significant(cursor.getString(7).trim().equalsIgnoreCase("Y")?true:false);

	    	aq.setBadness_indicators(cursor.getString(8));
	    	aq.setMean_values(cursor.getString(9));
	    	aq.setPredicted_values(cursor.getString(10));

		    // Adding entry to list
			aqmresults_list.add(aq);
		} while (cursor.moveToNext());
	    }

	    // return entry list
	    cursor.close();
	    db.close();	    
	} catch (Exception e) {
	    // TODO: handle exception
	    Log.e("all_aqmresults", "" + e);
	}

	return aqmresults_list;
    }

    // Updating single entry
    public int Update_AQMResult(AQMResults aq) {
	SQLiteDatabase db = this.getWritableDatabase();
	
	
	ContentValues values = new ContentValues();
	values.put(KEY_comparison_type, (String)aq.getComparison_type()); 
	values.put(KEY_analysis_starttime, getDateTimeAsString(aq.getAnalysis_starttime())); 
	values.put(KEY_analysis_endtime, getDateTimeAsString(aq.getAnalysis_endtime())); 
	values.put(KEY_is_btw_grp_significant, aq.isIs_btw_grp_significant()?"Y":"N"); 
	values.put(KEY_is_within_grp_significant, aq.isIs_within_grp_significant()?"Y":"N"); 
	values.put(KEY_is_interaction_significant, aq.isIs_interaction_significant()?"Y":"N"); 
	values.put(KEY_is_lineartrend_significant, aq.isIs_lineartrend_significant()?"Y":"N"); 
	values.put(KEY_badness_indicators, (String) aq.getBadness_indicators()); 
	values.put(KEY_mean_values, (String) aq.getMean_values()); 
	values.put(KEY_predicted_values, (String) aq.getPredicted_values()); 
	 

	// updating row
	return db.update(TABLE_GENERIC, values, KEY_ID + " = ?",
		new String[] { String.valueOf(aq.getId()) });
    }

    // Deleting single entry
    public void Delete_AQMResults(int id) {
	SQLiteDatabase db = this.getWritableDatabase();
	db.delete(TABLE_GENERIC, KEY_ID + " = ?",
		new String[] { String.valueOf(id) });
	db.close();
    }
    
    public void Delete_All_AQMResults() {
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.delete(TABLE_GENERIC, KEY_ID + " > ?",
    		new String[] { String.valueOf(0) });
    	db.close();
        }

    // Getting DylosFeeds Count
    public int Get_Total_AqmResults() {
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
    
    // Getting max DylosFeeds id
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
    
    // Getting max DylosFeeds id
    public Timestamp Get_Max_AqmAnalysisEndTime() {
    	Timestamp retValue = null; 
    Log.d(TAG, "Value returned form Get_Total_AqmResults() "+Get_Total_AqmResults());
    if (Get_Total_AqmResults() > 0){
    	String countQuery = "SELECT  " + KEY_analysis_endtime + " FROM " + TABLE_GENERIC +" ORDER BY "+KEY_analysis_endtime+ " DESC";
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.rawQuery(countQuery, null);
    	if (cursor.moveToFirst()) {
    		retValue = parseString2TimeStamp(cursor.getString(0));
    	}	
    	cursor.close();	
    	db.close();
    }
	// return count
	return retValue;
    }

}
