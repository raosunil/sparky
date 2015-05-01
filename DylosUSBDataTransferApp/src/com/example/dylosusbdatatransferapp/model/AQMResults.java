package com.example.dylosusbdatatransferapp.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import android.util.Log;





/**
 * 
 * @author sunilrao
 *
 *
 *mysql> describe aqm_data_results;
+----------------------------+--------------+------+-----+---------------------+-----------------------------+
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
12 rows in set (0.01 sec)
 */
public class AQMResults {
	
	private int id;
	public enum Aqi_Category {GREEN, YELLOW, ORANGE, RED, PURPLE};
	private String comparison_type;
	private Timestamp analysis_starttime;
	private Timestamp analysis_endtime;
	private boolean is_btw_grp_significant;
	private boolean is_within_grp_significant;
	private boolean is_interaction_significant;
	private boolean is_lineartrend_significant;
	private String badness_indicators;
	private String mean_values;
	private String predicted_values;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getComparison_type() {
		return comparison_type;
	}
	public void setComparison_type(String comparison_type) {
		this.comparison_type = comparison_type;
	}
	public Timestamp getAnalysis_starttime() {
		return analysis_starttime;
	}
	public void setAnalysis_starttime(Timestamp analysis_starttime) {
		this.analysis_starttime = analysis_starttime;
	}
	public Timestamp getAnalysis_endtime() {
		return analysis_endtime;
	}
	public void setAnalysis_endtime(Timestamp analysis_endtime) {
		this.analysis_endtime = analysis_endtime;
	}
	public boolean isIs_btw_grp_significant() {
		return is_btw_grp_significant;
	}
	public void setIs_btw_grp_significant(boolean is_btw_grp_significant) {
		this.is_btw_grp_significant = is_btw_grp_significant;
	}
	public boolean isIs_within_grp_significant() {
		return is_within_grp_significant;
	}
	public void setIs_within_grp_significant(boolean is_within_grp_significant) {
		this.is_within_grp_significant = is_within_grp_significant;
	}
	public boolean isIs_interaction_significant() {
		return is_interaction_significant;
	}
	public void setIs_interaction_significant(boolean is_interaction_significant) {
		this.is_interaction_significant = is_interaction_significant;
	}
	public boolean isIs_lineartrend_significant() {
		return is_lineartrend_significant;
	}
	public void setIs_lineartrend_significant(boolean is_lineartrend_significant) {
		this.is_lineartrend_significant = is_lineartrend_significant;
	}
	public String getBadness_indicators() {
		return badness_indicators;
	}
	public void setBadness_indicators(String badness_indicators) {
		this.badness_indicators = badness_indicators;
	}
	public String getMean_values() {
		return mean_values;
	}
	public void setMean_values(String mean_values) {
		this.mean_values = mean_values;
	}
	public String getPredicted_values() {
		return predicted_values;
	}
	public void setPredicted_values(String predicted_values) {
		this.predicted_values = predicted_values;
	}
	
	public String toString(){
		//return analysis_endtime+" : "+ analysis_starttime +":"+mean_values+":"+ badness_indicators;
		Aqi_Category aq_cat_severe_overall = calculateAQICategory(mean_values);
		if (aq_cat_severe_overall != null)
			return "EPA Air Quality Index Category is : "+aq_cat_severe_overall.name() + " observed at " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(analysis_starttime);
		else 
			return "Not good";
		
	}
	
	private Aqi_Category calculateAQICategory(String mean_values){
		
		Aqi_Category most_server_aqi_cat = Aqi_Category.GREEN;
		try{
			
			//System.out.println(user);
			String[] mean_Array = getMean_values().substring(1).split("\\|");
			int n_r = mean_Array.length;
			int inc = 0;
			String devices[] = getComparison_type().substring(1).split("\\|");
			int n_b = devices.length;
			int n_w = n_r/n_b;
			double mean_per_block = 0;
			for (String str:mean_Array){
				Aqi_Category aqi_cat = Aqi_Category.GREEN;
				if (str.trim().length() == 0)
					continue;
				mean_per_block = mean_per_block + Double.valueOf(str);
				if (inc>0&&inc%n_w==0){
					mean_per_block = mean_per_block/n_w;
					double concentration_pm25 = Math.pow(Math.E, mean_per_block);
					if (concentration_pm25<=15.4)
						aqi_cat = Aqi_Category.GREEN;
					else if (concentration_pm25>15.4 && concentration_pm25<=35.4 )
						aqi_cat = Aqi_Category.YELLOW;
					else if (concentration_pm25>35.4 && concentration_pm25<=65.4 )
						aqi_cat = Aqi_Category.ORANGE;
					else if (concentration_pm25>65.4 && concentration_pm25<=150.4 )
						aqi_cat = Aqi_Category.RED;
					else 
						aqi_cat = Aqi_Category.PURPLE;
				}

				if (aqi_cat.ordinal() > most_server_aqi_cat.ordinal())
					most_server_aqi_cat = aqi_cat;

				inc++;	
			}
		}catch (Exception e){
			Log.d("AQMResults", e.getLocalizedMessage());
		}
			return most_server_aqi_cat;
	}
	
}
