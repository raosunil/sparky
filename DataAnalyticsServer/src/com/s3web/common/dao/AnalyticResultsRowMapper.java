package com.s3web.common.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.s3web.common.beans.*;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * @author sunilrao
 * 
 * 
 * select RES_ID,analysis_starttime,analysis_endtime,
 * is_btw_grp_significant,is_within_grp_significant,is_interaction_significant,
 * is_lineartrend_significant,badness_indicators,mean_values,
 * predicted_values,modified_timestamp from aqm_data_results 
	
 *
 */
public class AnalyticResultsRowMapper implements ParameterizedRowMapper<AQMResults> {

	@Override
	public AQMResults mapRow(ResultSet rSet, int count) throws SQLException {
		// TODO Auto-generated method stub
		AQMResults result = new AQMResults();
		result.setId(rSet.getInt("RES_ID"));
		result.setComparison_type(rSet.getString("comparison_type"));
		result.setAnalysis_starttime(rSet.getTimestamp("analysis_starttime"));
		result.setAnalysis_endtime(rSet.getTimestamp("analysis_endtime"));
		result.setIs_btw_grp_significant(rSet.getString("is_btw_grp_significant").equalsIgnoreCase("Y"));
		result.setIs_within_grp_significant(rSet.getString("is_within_grp_significant").equalsIgnoreCase("Y"));
		result.setIs_interaction_significant(rSet.getString("is_interaction_significant").equalsIgnoreCase("Y"));
		result.setIs_lineartrend_significant(rSet.getString("is_lineartrend_significant").equalsIgnoreCase("Y"));
		result.setBadness_indicators(rSet.getString("badness_indicators"));
		result.setMean_values(rSet.getString("mean_values"));
		result.setPredicted_values(rSet.getString("predicted_values"));
		return result;
	}
}
