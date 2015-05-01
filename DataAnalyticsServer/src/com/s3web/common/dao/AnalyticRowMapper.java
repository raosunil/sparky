package com.s3web.common.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.s3web.common.beans.*;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class AnalyticRowMapper implements ParameterizedRowMapper<AQMData> {

	@Override
	public AQMData mapRow(ResultSet rSet, int count) throws SQLException {
		// TODO Auto-generated method stub
		
		/*MyData mydata = new MyData();
		
		mydata.setId(rSet.getInt("id"));
		mydata.setDescription(rSet.getString("description"));*/
		//TODO - write imlementation
		
		AQMData aqm = new AQMData();
		aqm.setId(rSet.getInt("FMAQM_ID"));
		aqm.setDatetime(rSet.getLong("datetime"));
		aqm.setBlock(rSet.getString("block"));
		aqm.setTime(rSet.getInt("time"));
		aqm.setSmallParticle(rSet.getInt("smallParticle"));
		aqm.setLargeParticle(rSet.getInt("largeParticle"));
		aqm.setConv_epa_smallParticle(rSet.getBigDecimal("conv_epa_smallParticle"));
		aqm.setConv_epa_largeParticle(rSet.getBigDecimal("conv_epa_largeParticle"));
		aqm.setLn_smallParticle(rSet.getBigDecimal("ln_smallParticle"));
		aqm.setLn_largeParticle(rSet.getBigDecimal("ln_largeParticle"));
		aqm.setMinuteofday(rSet.getInt("minuteofday"));
		return aqm;
	}

}
