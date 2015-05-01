package com.s3web.common.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.s3web.common.beans.*;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class RegressionRowMapper implements ParameterizedRowMapper<List<Double>> {

	@Override
	public List<Double> mapRow(ResultSet rSet, int count) throws SQLException {
		
		List<Double> enclosedList = new ArrayList<Double>();
		enclosedList.add((double) rSet.getInt("minuteofday"));
		enclosedList.add(rSet.getDouble("ln_smallParticle"));
		return enclosedList;
	}

}
