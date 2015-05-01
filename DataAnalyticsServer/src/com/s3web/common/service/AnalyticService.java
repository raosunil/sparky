package com.s3web.common.service;

import java.math.BigDecimal;
import java.util.List;

import com.s3web.common.beans.AQMData;
import com.s3web.common.beans.AQMResults;

public interface AnalyticService {
	
	public void fetchDesignMatrixAQMData();
	
	public List<AQMData> fetchAQMData();
	
	public List<String> getUniqueDevices();
	
	public List<List<BigDecimal>>getFourPreDeterminedConstrastCoefficients();
	
	public BigDecimal calculateSumOfSquares(String type);
	
	public BigDecimal calculateErrorSumofSquares();
	
	public void doAnalysis();
	
	public List<AQMResults> fetchAQMResults();
	
	public void persistInputRawData();
}
