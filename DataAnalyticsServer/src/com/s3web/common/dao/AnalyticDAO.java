package com.s3web.common.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.s3web.common.beans.AQMData;
import com.s3web.common.beans.AQMResults;
import com.s3web.common.service.AnalyticServiceImpl;

public class AnalyticDAO  {
	
	private Logger logger = Logger.getLogger(AnalyticDAO.class);
	
	
	private JdbcTemplate jdbcTemplate;
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);  
	}

	

	
	public List<List<Double>> getAQMDataForRegression(String block){ 
		Object[] args = {block};
		return (List<List<Double>>) jdbcTemplate.query("select minuteofday,ln_smallParticle from final_merged_aqmdata where block = ?   order by datetime",args,new RegressionRowMapper());
	}
	
	public List<java.lang.Double> getAQMDataResponseVariable(String block, long start, int count){ 
		Object[] args = {block,start,count};
		return (List<java.lang.Double>) jdbcTemplate.queryForList("select ln_smallParticle from final_merged_aqmdata where block = ? and datetime > ?  order by datetime LIMIT ?",args,java.lang.Double.class);
	}
	
	public List<AQMData> getAQMData(long start, int count){  
		Object[] args = {start,count};
		logger.debug("Value of start and count is : "+ start +  " " + count);
		return (List<AQMData>) jdbcTemplate.query("select datetime,block,smallParticle,largeParticle,FMAQM_ID,time,conv_epa_smallParticle,conv_epa_largeParticle,ln_smallParticle,ln_largeParticle,minuteofday from final_merged_aqmdata where datetime > ? order by datetime LIMIT ?",args, new AnalyticRowMapper());
		
	}
	
	public void insertAQMData(Object[] insertargs){
		String sql = "insert into final_merged_aqmdata(datetime,minuteofday,block,smallParticle,largeParticle,time,conv_epa_smallParticle,conv_epa_largeParticle,ln_smallParticle,ln_largeParticle)"
				+ " values(?,?,?,?,?,?,?,?,?,?)";
			
			jdbcTemplate.update(sql, insertargs);
	}
	
	public List<String> getUniqueBlocks(long start, long end ){
		Object[] args={start, end};
		List<String> results = null;
		try {
			results = (List<String>) jdbcTemplate.queryForList("select distinct block from final_merged_aqmdata where datetime > ?  and datetime < ? order by datetime ",args,String.class);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			logger.debug("Exception in getUniqueBlocks DAO : "+sw.toString() );
		}
		return results;
	}
	
	public Timestamp getMaxEndTime(){
		return (Timestamp) jdbcTemplate.queryForObject("select max(analysis_endtime) from aqm_data_results", java.sql.Timestamp.class);
	}
	
	public void insertAnalyticResults(Object[] args){
		String sql = "insert into aqm_data_results(comparison_type, analysis_starttime, analysis_endtime," +
				" is_btw_grp_significant, is_within_grp_significant, is_interaction_significant, is_lineartrend_significant, "+
				" badness_indicators, mean_values, predicted_values,actual_p_values) values (?,?,?,?,?,?,?,?,?,?,?)";
			
			jdbcTemplate.update(sql, args);
	}
	
	public List<AQMResults> getAQMResults(){  
		return (List<AQMResults>) jdbcTemplate.query("select RES_ID,comparison_type,analysis_starttime,analysis_endtime,is_btw_grp_significant,is_within_grp_significant,is_interaction_significant,is_lineartrend_significant,badness_indicators,mean_values,predicted_values,modified_timestamp from aqm_data_results order by modified_timestamp ", new AnalyticResultsRowMapper());
	}
	
	public void insertConfigurationPair(String key, String value){
		Object[] args = {key,value};
		String sql= "insert into configuration(name,value) values (?,?)";
		jdbcTemplate.update(sql, args);
		
	}
	
	public String retrieveConfigurationValueForKey(String key){
		String retValue= null;
		try{
		Object[] args = {key};
		retValue  =  (String)jdbcTemplate.queryForObject("select VALUE from configuration where NAME = ? ", args, String.class);
		}catch (Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			logger.debug("Exception in retrieveConfigurationValueForKey DAO : "+sw.toString() );
		}
		return retValue;
	}
	
	
	
	

}
