package com.example.dylosusbdatatransferapp.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.example.dylosusbdatatransferapp.model.DylosReading;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Sunil
 *DAO class 
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version 
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "dylosManager";

    // Dylos table name
    private static final String TABLE_GENERIC = "dylosFeed";

    // DylosFeeds Table Columns names
    
    /*DylosFeed
     * 
     * private String deviceId;
    private String userId;
    private String dateTime;
    private int smallParticleCount;
    private int largeParticleCount;
    private double geoLatitude;
    private double geoLongitude;
    */
    private static final String KEY_ID = "id";
    private static final String KEY_DEVICEID = "deviceId";
    private static final String KEY_USERID = "userId";
    private static final String KEY_DATETIME = "dateTime";
    private static final String KEY_SMALLPARTICLECOUNT = "smallParticle";
    private static final String KEY_LARGEPARTICLECOUNT = "largeParticle";
    private static final String KEY_GEOLATITUDE = "geoLatitude";
    private static final String KEY_GEOLONGITUDE = "geoLongitude";
    private static final String KEY_GEOMETHOD = "method";
   
    private final ArrayList<DylosReading> dylosFeed_list = new ArrayList<DylosReading>();

    public DatabaseHandler(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	Log.d("DatabaseHandler","Inside onCreate ");
	String CREATE_IRC_TABLE = "CREATE TABLE " + TABLE_GENERIC + "("
		+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_DEVICEID + " TEXT,"
		+ KEY_USERID + " TEXT," + KEY_DATETIME + " INTEGER,"
		+ KEY_SMALLPARTICLECOUNT + " INTEGER," + KEY_LARGEPARTICLECOUNT + " INTEGER,"
		+ KEY_GEOLATITUDE + " REAL," + KEY_GEOLONGITUDE + " REAL,"
		+ KEY_GEOMETHOD + " TEXT "
		+ ")";
	db.execSQL(CREATE_IRC_TABLE);
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

    // Adding new DylosFeed
    public void Add_DylosReading(DylosReading dr) {
    Log.d("DatabaseHandler","Inside Add_DylosReading");
    
    Map properties = dr.getProperties();
    double[] coordinates = (double[])dr.getGeometry().get("coordinates");
    
    	
	SQLiteDatabase db = this.getWritableDatabase();
	
	ContentValues values = new ContentValues();
	values.put(KEY_DEVICEID, (String) properties.get(KEY_DEVICEID)); 
	values.put(KEY_USERID, (String) properties.get(KEY_USERID)); 
	values.put(KEY_DATETIME, Long.valueOf((Long)properties.get(KEY_DATETIME)).intValue()); 
	values.put(KEY_GEOMETHOD, (String) properties.get(KEY_GEOMETHOD)); 
	values.put(KEY_SMALLPARTICLECOUNT, (Integer) properties.get(KEY_SMALLPARTICLECOUNT)); 
	values.put(KEY_LARGEPARTICLECOUNT, (Integer) properties.get(KEY_LARGEPARTICLECOUNT)); 
	values.put(KEY_GEOLATITUDE, coordinates[0]); 
	values.put(KEY_GEOLONGITUDE, coordinates[1]); 
	
	
	
	// Inserting Row
	db.insert(TABLE_GENERIC, null, values);
	db.close(); // Closing database connection
    }

    // Getting single DylosFeed
    DylosReading Get_DylosFeed(int id) {
	SQLiteDatabase db = this.getReadableDatabase();

	Cursor cursor = db.query(TABLE_GENERIC, new String[] { KEY_ID,
			KEY_DEVICEID,KEY_USERID, KEY_DATETIME,KEY_SMALLPARTICLECOUNT,KEY_LARGEPARTICLECOUNT,KEY_GEOLATITUDE,KEY_GEOLONGITUDE,KEY_GEOMETHOD}, KEY_ID + "=?",
		new String[] { String.valueOf(id) }, null, null, null);
	if (cursor != null)
	    cursor.moveToFirst();

	DylosReading dylosFeed = new DylosReading(
		cursor.getString(1),cursor.getString(2),Integer.parseInt(cursor.getString(3)),
		Integer.parseInt(cursor.getString(4)),Integer.parseInt(cursor.getString(5)),Double.parseDouble(cursor.getString(6)),
		Double.parseDouble(cursor.getString(7)),cursor.getString(8));
	// return DylosFeed
	cursor.close();
	db.close();

	return dylosFeed;
    }

    // Getting All DylosFeeds
    public ArrayList<DylosReading> Get_DylosReadings() {
	try {
		dylosFeed_list.clear();

	    // Select All Query
	    String selectQuery = "SELECT  " + KEY_ID + "," + KEY_DEVICEID + "," 
	    				+ KEY_USERID + "," +  KEY_DATETIME + "," 
	    				+ KEY_SMALLPARTICLECOUNT + "," + KEY_LARGEPARTICLECOUNT + "," 
	    				+ KEY_GEOLATITUDE + "," + KEY_GEOLONGITUDE + "," + KEY_GEOMETHOD 
	    				+  " FROM " + TABLE_GENERIC;

	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);

	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
		do {
			DylosReading dylosFeed = new DylosReading(
					cursor.getString(1),cursor.getString(2),Integer.parseInt(cursor.getString(3)),
					Integer.parseInt(cursor.getString(4)),Integer.parseInt(cursor.getString(5)),Double.parseDouble(cursor.getString(6)),
					Double.parseDouble(cursor.getString(7)),cursor.getString(8));
			dylosFeed.setId(Integer.parseInt(cursor.getString(0)));
		
		    
		    // Adding entry to list
			dylosFeed_list.add(dylosFeed);
		} while (cursor.moveToNext());
	    }

	    // return entry list
	    cursor.close();
	    db.close();	    
	} catch (Exception e) {
	    // TODO: handle exception
	    Log.e("all_DylosFeeds", "" + e);
	}

	return dylosFeed_list;
    }

    // Updating single entry
    public int Update_DylosFeed(DylosReading dylosFeed) {
	SQLiteDatabase db = this.getWritableDatabase();
	
	Map properties = dylosFeed.getProperties();
    double[] coordinates = (double[])dylosFeed.getGeometry().get("coordinates");

	ContentValues values = new ContentValues();
	values.put(KEY_DEVICEID, (String) properties.get(KEY_DEVICEID)); 
	values.put(KEY_USERID, (String) properties.get(KEY_USERID)); 
	values.put(KEY_DATETIME, (Integer) properties.get(KEY_DATETIME)); 
	values.put(KEY_GEOMETHOD, (String) properties.get(KEY_GEOMETHOD)); 
	values.put(KEY_SMALLPARTICLECOUNT, (Integer) properties.get(KEY_SMALLPARTICLECOUNT)); 
	values.put(KEY_LARGEPARTICLECOUNT, (String) properties.get(KEY_LARGEPARTICLECOUNT)); 
	values.put(KEY_GEOLATITUDE, coordinates[0]); 
	values.put(KEY_GEOLONGITUDE, coordinates[1]); 
	
	

	// updating row
	return db.update(TABLE_GENERIC, values, KEY_ID + " = ?",
		new String[] { String.valueOf(dylosFeed.getId()) });
    }

    // Deleting single entry
    public void Delete_DylosFeed(int id) {
	SQLiteDatabase db = this.getWritableDatabase();
	db.delete(TABLE_GENERIC, KEY_ID + " = ?",
		new String[] { String.valueOf(id) });
	db.close();
    }

    // Getting DylosFeeds Count
    public int Get_Total_DylosFeeds() {
    int retValue = -1;   
	String countQuery = "SELECT  COUNT(1) FROM " + TABLE_GENERIC;
	SQLiteDatabase db = this.getReadableDatabase();
	Cursor cursor = db.rawQuery(countQuery, null);
	retValue = cursor.getCount();
	cursor.close();	
	db.close();
	// return count
	return retValue;
    }

}
