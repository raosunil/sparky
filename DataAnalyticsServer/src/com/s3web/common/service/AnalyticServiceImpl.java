package com.s3web.common.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.s3web.common.beans.AQMData;
import com.s3web.common.beans.AQMResults;
import com.s3web.common.dao.AnalyticDAO;
import com.s3web.util.ApplicationContextProvider;

public class AnalyticServiceImpl implements AnalyticService {
	
	private AnalyticDAO analyticDAO;
	private Map applicationSettings = null;
	//start_analysis - epoch time in sec
	private long start_analysis = 0;
	private long end_analysis = 0;
	private int n_w;
	private int n_b;
	private int numberOfGroups_r;
	private int n_i;
	private double[][] oneway_anova = null;
	private String comparison_type = "";
	private static final int RESTRICT_COUNT_RESULTS = 10;
	
	private Logger logger = Logger.getLogger(AnalyticServiceImpl.class);
	//ln(12 micro gram /cubic meter)
	private static final double EPA_STANDARD_THRESHOLD_PM25 = 2.48491;
	private static final double CONVERSION_FT_CUBE_METER_CUBE = 3531.5 ;
	private static final double AVG_SPHERIAL_MASS_AIR_PARTICLE = 5.89E-7 ;

	private ApplicationContext context =  ApplicationContextProvider.getApplicationContext();
	public void setAnalyticDAO(AnalyticDAO analyticDAO) {
		this.analyticDAO = analyticDAO;
	}

	private void initial() {
		try{
			if (applicationSettings == null)
				applicationSettings = (Map)context.getBean("applicationproperties");
			logger.debug("Inside initial ....");
			Timestamp last_analysis = analyticDAO.getMaxEndTime();
		
			if (last_analysis == null){
				String dateasString = (String) applicationSettings.get("startAnalysisEpochTimeinSec");
				logger.debug("String obtained from properties" + dateasString );
				Long start = null;
				try {
					start = Long.valueOf(dateasString);
				} catch (Exception e) {
					logger.debug("Error parsing "+e.getLocalizedMessage());
				}
				long last_analysis_sec = 0;
				if (start != null){
					last_analysis_sec = start;
				}else{
					last_analysis_sec = System.currentTimeMillis()/1000;
				}
				last_analysis = new Timestamp(((AQMData)analyticDAO.getAQMData(
						last_analysis_sec,1).get(0)).getDatetime());
				start_analysis = last_analysis.getTime();
			}else{
				logger.debug("Printing last end analsis time " + last_analysis.toString());
				start_analysis = last_analysis.getTime()/1000;
			}
			logger.debug("start_analysis value is " + start_analysis );
			n_w = Integer.valueOf((String) applicationSettings.get("numGroupsWithin1WayANOVA"));
			logger.debug("n_w from properties" + n_w );
			List<String> devices = getUniqueDevices();
			//Resetting back
			comparison_type ="";
			for (String str: devices){
				comparison_type = comparison_type + "|" + str;
			}
				
			n_b = devices.size();
			logger.debug("n_b from properties" + n_b );
			numberOfGroups_r = n_w * n_b;
			logger.debug("numberOfGroups_r from properties" + numberOfGroups_r );
			n_i = Integer.valueOf((String) applicationSettings.get("sampleSizeForEachGroup"));
			logger.debug("n_i from properties" + n_i );
		}catch(Exception e){
			logger.debug("Exception in initial : "+e.getLocalizedMessage());
		}

	}

	public void fetchDesignMatrixAQMData() {
		try{
			logger.debug("Inside fetchDesignMatrixAQMData : " );
			if (oneway_anova == null ){
				
				oneway_anova = new double[numberOfGroups_r][n_i];
				
				
				
				List<String> devices = getUniqueDevices();
				int increment = 0;
				for (String str: devices){
					
					
					List<Double> temp = analyticDAO.getAQMDataResponseVariable(str, start_analysis,n_w*n_i);
					
					/*Time window split*/
					int initial = 0;
				    for (int time=1; time<=n_w; time++){
				    	List<Double> sublist = new ArrayList();
				    	sublist = new ArrayList<Double>(temp.subList(initial, temp.size()*time/n_w));
				    	
				    	for (int j=0; j< sublist.size();j++){
				    		oneway_anova[increment][j] = sublist.get(j);
				    	}
				    	increment++;
				    	initial = initial + temp.size()*time/n_w;
				    }
		
				}
				for (int j=0; j<n_i;j++){
					//logger.debug("Testing if it has passed: "+ oneway_anova[0][j] + " | "+oneway_anova[1][j] +" | "+oneway_anova[2][j] +" | "+oneway_anova[3][j]);
					//logger.debug("Testing if it has passed: "+ input_variable[0][j] + " | "+input_variable[1][j] +" | "+input_variable[2][j] +" | "+input_variable[3][j]);
				}
	
			}
		}catch (Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			logger.debug("Exception in fetchDesignMatrixAQMData : "+sw.toString() );
		}

	}

	@Override
	public List<String> getUniqueDevices() {
		//One hour analysis window relative to end_time of previous analysis
		long end_analysis_time_sec = start_analysis + 3600;
		return analyticDAO.getUniqueBlocks(start_analysis,end_analysis_time_sec);
	}

	/* (non-Javadoc)
	 * 
	 * The coefficients obtained are for the following Contrasts
	 * a) Is the difference between the mean response of small particulate count 
	 * across all devices uniform? This is called A.
	 * b) Is the difference between the mean response of small particulate count 
	 * across two time windows of half an hour each, uniform? This is called B
	 * c) Is there any interaction between device and time window? This is called AB
	 * 
	 * 
	 * @see com.s3web.common.service.AnalyticService#getThreePreDeterminedConstrastCoefficients()
	 */
	@Override
	public List<List<BigDecimal>> getFourPreDeterminedConstrastCoefficients() {
	
				
				List<List<BigDecimal>> wrapperList = new ArrayList<List<BigDecimal>>(4);
				List<BigDecimal> enclosedList_A = new ArrayList<BigDecimal>(numberOfGroups_r);
				List<BigDecimal> enclosedList_B = new ArrayList<BigDecimal>(numberOfGroups_r);
				List<BigDecimal> enclosedList_AB = new ArrayList<BigDecimal>(numberOfGroups_r);
				//performed later
				List<BigDecimal> enclosedList_linear = new ArrayList<BigDecimal>(numberOfGroups_r);
				//sign_coeffecient
				int a = +1;
				
				for(int i = 0; i < numberOfGroups_r; i++){
					if (i<n_b){
						enclosedList_A.add(new BigDecimal(a*(1/n_b)));
					}
					else{
						enclosedList_A.add(new BigDecimal(-a*(1/n_b)));
					}

					if (i%2==0){
						enclosedList_B.add(new BigDecimal(a*(1/n_b)));
					}
					else{
						enclosedList_B.add(new BigDecimal(-a*(1/n_b)));
					}
					if (i == 0 || i == (numberOfGroups_r - 1) ){
						enclosedList_AB.add(new BigDecimal(1));
					}else{
						enclosedList_AB.add(new BigDecimal(-a*(2/numberOfGroups_r - 2)));
					}
					if (i<n_w){
						if (i%2 == 0)
							enclosedList_linear.add(new BigDecimal(a));
						else
							enclosedList_linear.add(new BigDecimal(-a));
					}else{
						if (i%2 == 0)
							enclosedList_linear.add(new BigDecimal(a));
						else
							enclosedList_linear.add(new BigDecimal(-a));
					}

				}
				wrapperList.add(enclosedList_A);
				wrapperList.add(enclosedList_B);
				wrapperList.add(enclosedList_AB);
				//performed later
				wrapperList.add(enclosedList_linear);
		return wrapperList;
	}

	@Override
	public BigDecimal calculateSumOfSquares(String type) {

		List<List<BigDecimal>> contrast = getFourPreDeterminedConstrastCoefficients();
		BigDecimal result = new BigDecimal(0);
		MathContext mc = new MathContext(4, RoundingMode.HALF_EVEN);
		try{
			if (oneway_anova == null)
				fetchDesignMatrixAQMData();
			if (type.equalsIgnoreCase("A")){
				//by convention first is contrast A
				List<BigDecimal> enclosedList_A = contrast.get(0);
				BigDecimal denom = new BigDecimal(0);
				for (int i=0; i< numberOfGroups_r;i++){
					result = result.add(enclosedList_A.get(i).multiply(new BigDecimal(StatUtils.mean(oneway_anova[i]))));
					denom = denom.add(enclosedList_A.get(i).pow(2).divide(new BigDecimal(n_i)));
				}
				if (denom.compareTo(new BigDecimal(0)) != 0)
						result = result.pow(2).divide(denom, mc);   
			}else if (type.equalsIgnoreCase("B")){
				//by convention first is contrast B
				List<BigDecimal> enclosedList_B = contrast.get(1);
				BigDecimal denom = new BigDecimal(0);
				for (int i=0; i< numberOfGroups_r;i++){
					result = result.add(enclosedList_B.get(i).multiply(new BigDecimal(StatUtils.mean(oneway_anova[i]))));
					denom = denom.add(enclosedList_B.get(i).pow(2).divide(new BigDecimal(n_i)));
				}
				if (denom.compareTo(new BigDecimal(0)) != 0)
					result = result.pow(2).divide(denom, mc);   
			}else if (type.equalsIgnoreCase("AB")){
				//by convention first is contrast AB
				List<BigDecimal> enclosedList_AB = contrast.get(2);
				BigDecimal denom = new BigDecimal(0);
				for (int i=0; i< numberOfGroups_r;i++){
					result = result.add(enclosedList_AB.get(i).multiply(new BigDecimal(StatUtils.mean(oneway_anova[i]))));
					denom = denom.add(enclosedList_AB.get(i).pow(2).divide(new BigDecimal(n_i)));
				}
				if (denom.compareTo(new BigDecimal(0)) != 0){
					result = result.pow(2).divide(denom,  mc);
				}
			}
			else{
				List<BigDecimal> enclosedList_linear = contrast.get(3);
				BigDecimal denom = new BigDecimal(0);
				for (int i=0; i< numberOfGroups_r;i++){
					result = result.add(enclosedList_linear.get(i).multiply(new BigDecimal(StatUtils.mean(oneway_anova[i]))));
					denom = denom.add(enclosedList_linear.get(i).pow(2).divide(new BigDecimal(n_i)));
				}
				if (denom.compareTo(new BigDecimal(0)) != 0)
					result = result.pow(2).divide(denom,  mc); 
			}
		} catch (Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			logger.debug("Exception in calculateSumOfSquares : "+sw.toString() );
		}
		return result;
	}

	@Override
	public BigDecimal calculateErrorSumofSquares() {
		
		BigDecimal result = new BigDecimal(0);
		try{
		for (int i = 0; i< numberOfGroups_r; i++){
			result = result.add(new BigDecimal(n_i - 1).multiply(new BigDecimal(StatUtils.variance(oneway_anova[i]))));
		}
		}catch (Exception e){
			
		}
		
		return result;
	}

	@Override
	public void doAnalysis() {
		try{
			
			logger.debug("Calling initial again");
			initial();
			// TODO Auto-generated method stub
			//Using Sidak equation - where Pc is 
			//pvalue for individual contrast and Pf is family pvalue
			//α[Pc] = 1 − (1 − α[Pf])**1/c - where c=3 is number of comparisons
			//Keeping family pvlue = 0.05
			//Since contrasts are orthogonal to each other; they are independent
			
			//Precision set to 4 
			
			
			MathContext mc = new MathContext(4, RoundingMode.HALF_EVEN);
			double pc = 0.017;
			logger.debug("Constant values are " + numberOfGroups_r +"  " +n_i +"  "+ n_b+"  "+start_analysis+"  ");
			FDistribution fdist = new FDistribution(1,(n_i * numberOfGroups_r) - numberOfGroups_r);
			double critical_value = fdist.inverseCumulativeProbability(0.88);
			
			String actual_pValues_doubleString= "";

			//Initially doing for 3 Contrasts
			BigDecimal F_value_A = new BigDecimal(0);
			BigDecimal F_value_B = new BigDecimal(0);
			BigDecimal F_value_AB = new BigDecimal(0);
			BigDecimal F_value_linear = new BigDecimal(0);

			//Since contrast has degree of freedom = 1
			F_value_A = F_value_A.add(calculateSumOfSquares("A").
					divide(calculateErrorSumofSquares().
							divide(new BigDecimal((n_i * numberOfGroups_r) - numberOfGroups_r),mc),mc));
			//Testing between the groups
			boolean is_A_significant = F_value_A.compareTo(new BigDecimal(critical_value)) > 0 ? true:false;
			logger.debug("Printing the value of is_A_significant : " + is_A_significant);
			actual_pValues_doubleString = actual_pValues_doubleString + "|" + (1.0 - fdist.cumulativeProbability(Double.valueOf(F_value_A.doubleValue())));


			F_value_B = F_value_B.add(calculateSumOfSquares("B").
					divide(calculateErrorSumofSquares().
							divide(new BigDecimal((n_i * numberOfGroups_r) - numberOfGroups_r),mc),mc));

			//Testing within the groups
			boolean is_B_significant = F_value_B.compareTo(new BigDecimal(critical_value)) > 0 ? true:false;
			logger.debug("Printing the value of is_B_significant : " + is_B_significant);
			actual_pValues_doubleString = actual_pValues_doubleString + "|" + (1.0 - fdist.cumulativeProbability(Double.valueOf(F_value_B.doubleValue())));

			F_value_AB = F_value_AB.add(calculateSumOfSquares("AB").
					divide(calculateErrorSumofSquares().
							divide(new BigDecimal((n_i * numberOfGroups_r) - numberOfGroups_r),mc),mc));
			//Testing no interaction
			boolean is_AB_significant = F_value_AB.compareTo(new BigDecimal(critical_value)) > 0 ? true:false;
			logger.debug("Printing the value of is_AB_significant : " + is_AB_significant);
			actual_pValues_doubleString = actual_pValues_doubleString + "|" + (1.0 - fdist.cumulativeProbability(Double.valueOf(F_value_AB.doubleValue())));
			
			//Additional one for future scope
			F_value_linear = F_value_linear.add(calculateSumOfSquares("linear").
					divide(calculateErrorSumofSquares().
							divide(new BigDecimal((n_i * numberOfGroups_r) - numberOfGroups_r),mc),mc));

			boolean is_lineartrend_significant = F_value_linear.compareTo(new BigDecimal(critical_value)) > 0 ? true:false;
			logger.debug("Printing the value of is_linear_significant : " + is_lineartrend_significant);
			actual_pValues_doubleString = actual_pValues_doubleString + "|" + (1.0 - fdist.cumulativeProbability(Double.valueOf(F_value_linear.doubleValue())));
			
			//Regression analysis for last one hour the ability to predict 
			//the aqm reading 1 hour 5 minutes (39000000 in ms) is as follows 
			SimpleRegression sreg = new SimpleRegression();
			Calendar cal = Calendar.getInstance();
			long start_time_millis = start_analysis * 1000l;
			cal.setTimeInMillis(start_time_millis  +  3900000l);
			double derived_minutes_of_day = cal.get(Calendar.MINUTE) + cal.get(Calendar.HOUR_OF_DAY) * 60;
			String badness_bool_string = "";
			String means_double_string = "";
			String predicted_double_string = "";

			for (String str: getUniqueDevices()){
				List<List<Double>> listofLists = analyticDAO.getAQMDataForRegression(str);
				for (List<Double> l: listofLists){
					sreg.addData(l.get(0), l.get(1));
				}
				sreg.regress();
				predicted_double_string = predicted_double_string + "|" + String.valueOf(sreg.predict(derived_minutes_of_day));
				logger.debug("Intercept val : "+sreg.getIntercept());
				logger.debug("Slope val : "+sreg.getSlope());
				sreg.clear();

			}

			for (int i=0; i< numberOfGroups_r; i++){
				Boolean test_bad = false;
				double mean = StatUtils.mean(oneway_anova[i]);
				//One sample t-test exceeding a given threshold
				double std_err = Math.pow(StatUtils.variance(oneway_anova[i]),0.5)/ (Math.pow(n_i,0.5)) ;
				TDistribution tdist = new TDistribution(n_i - 1);
				double one_sided_p_value = 1.0 - tdist.cumulativeProbability((mean - EPA_STANDARD_THRESHOLD_PM25)/std_err);
				test_bad = (one_sided_p_value < 0.05) ? true:false;
				badness_bool_string = badness_bool_string + "|" + test_bad.toString();
				means_double_string = means_double_string + "|" + mean;

			}

			logger.debug("Printing the values of badness, regression and means: "+badness_bool_string +" , " + predicted_double_string +" , "+means_double_string);

			/*insert into db
			 * 
			 * (comparison_type, analysis_starttime, analysis_endtime,
is_btw_grp_significant, is_within_grp_significant, is_interaction_significant, 
is_lineartrend_significant, badness_indicators, mean_values, predicted_values)
			 * */

			Object[] insertargs = {comparison_type,new Timestamp(start_analysis * 1000), new Timestamp((start_analysis + 3600) * 1000), 
					is_A_significant?"Y":"N",is_B_significant?"Y":"N",is_AB_significant?"Y":"N",is_lineartrend_significant?"Y":"N",
							badness_bool_string,means_double_string,predicted_double_string,actual_pValues_doubleString};
			analyticDAO.insertAnalyticResults(insertargs);
		}catch (Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			logger.debug("Exception in fetchAnovaResults : "+sw.toString() );
		}

	}
	
	public List<AQMResults> fetchAQMResults(){
		return analyticDAO.getAQMResults();
	}
	
	
	public List<AQMData> fetchAQMData(){
		return analyticDAO.getAQMData(start_analysis, n_i);
	}
	
	
	public void persistInputRawData(){
		logger.debug("Inside persistInputRawData");
		InputStream is = null;
	    BufferedReader buffRead = null;
		
		
	    try{
	    	String  urlString = (String) applicationSettings.get("rawinputURL");
	    	URL u = new URL(urlString);
	    	/*URLConnection conn = .openConnection();
	    	conn.setConnectTimeout(5000);
	        conn.setReadTimeout(10000);*/
	        boolean proxyEnabled = true;
	        URLConnection yc;
	        if(proxyEnabled) {
	            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("fpx0.com", 3128));
	            yc = u.openConnection(proxy);
	        } else {
	            yc = u.openConnection();
	        }
	 
	    	is = yc.getInputStream();
	    	buffRead = new BufferedReader(new InputStreamReader(is),4096);
	    	String str = null;
	    	double rh_factor = (Double)Double.valueOf((String) applicationSettings.get("avg_rh_factor"));
	    	String valueasString = analyticDAO.retrieveConfigurationValueForKey("avg_rh_factor");
	    	if (valueasString != null && valueasString.trim().length()>0){
	    		rh_factor = (Double)Double.valueOf(valueasString);
	    	}
	    	double correction_for_rh = (Double)Double.valueOf((String) applicationSettings.get("avg_correction_for_rh"));
	    	valueasString = analyticDAO.retrieveConfigurationValueForKey("avg_correction_for_rh");
	    	if (valueasString != null && valueasString.trim().length()>0){
	    		correction_for_rh = (Double)Double.valueOf(valueasString);
	    	}


	    	while((str=buffRead.readLine())!=null){
	    		if (str.indexOf("SpirometerReadings")>0||str.indexOf("spirometerreadings")>0
	    				||str.indexOf("AirQualityReadings")>0||str.indexOf("airqualityreadings")>0
	    				||str.indexOf("UIEvents")>0||str.indexOf("uievents")>0
	    				||str.trim().equalsIgnoreCase("")){
	    			continue;
	    		}
	    		String[] columnSubStrings = str.split(" ");
	    		logger.debug("Inside persistInputRawData columnSubString length " + columnSubStrings.length);
	    		if (columnSubStrings!=null && columnSubStrings.length==10){
	    			AQMData aqm = new AQMData();
	    			/*
	    			 * 
	    			 * Tue Apr 28 21:02:56 MST 2015 aspira2 aqm2 271 32
					   Tue Apr 28 21:05:54 MST 2015 aspira2 aqm2 277 30
	    			 */
	    			//0 to 5 (total 6 columns constitutes date

	    			String rawDateString = columnSubStrings[1] +" "
	    					+  columnSubStrings[2] +" " +  columnSubStrings[3] +" " 
	    					+  columnSubStrings[5];
	    			SimpleDateFormat sdf = new SimpleDateFormat("MMM d HH:mm:ss yyyy");
	    			Date date = sdf.parse(rawDateString);
	    			Calendar cal = Calendar.getInstance();
	    			cal.setTime(date);
	    			//Unix epoch time is in seconds not milliseconds
	    			aqm.setDatetime(date.getTime()/1000);
	    			//Doing this because there is no API for selecting data set after a
	    			//given timestamp
	    			if (analyticDAO.getMaxEndTime().after(date)){
	    				continue;
	    			}
	    			logger.debug("Inside persistInputRawData : Passed all continue");
	    			int hours_in_min = cal.get(Calendar.HOUR_OF_DAY) * 60;
	    			aqm.setMinuteofday(cal.get(Calendar.MINUTE) + hours_in_min);
	    			aqm.setBlock(columnSubStrings[6]);
	    			//Not using time - instead using numGroupsWithin1WayANOVA for time window within each block
	    			//Storing dummy value
	    			aqm.setTime(0);
	    			int smallParticleCount = Integer.valueOf(columnSubStrings[8]);
	    			aqm.setSmallParticle(smallParticleCount);
	    			int largeParticleCount = Integer.valueOf(columnSubStrings[9]);
	    			aqm.setLargeParticle(largeParticleCount);
	    			double conv_epa_smallParticle = smallParticleCount * CONVERSION_FT_CUBE_METER_CUBE * AVG_SPHERIAL_MASS_AIR_PARTICLE * rh_factor * correction_for_rh;
	    			aqm.setConv_epa_smallParticle(new BigDecimal(conv_epa_smallParticle));
	    			double conv_epa_largeParticle = largeParticleCount * CONVERSION_FT_CUBE_METER_CUBE * AVG_SPHERIAL_MASS_AIR_PARTICLE * rh_factor * correction_for_rh;
	    			aqm.setConv_epa_largeParticle(new BigDecimal(conv_epa_largeParticle));
	    			aqm.setLn_smallParticle(new BigDecimal(Math.log(conv_epa_smallParticle)));
	    			aqm.setLn_largeParticle(new BigDecimal(Math.log(conv_epa_largeParticle)));

	    			Object[] insertargs = {aqm.getDatetime(),aqm.getMinuteofday(),aqm.getBlock(),aqm.getSmallParticle(),
	    					aqm.getLargeParticle(),aqm.getTime(),aqm.getConv_epa_smallParticle(), aqm.getConv_epa_largeParticle(),
	    					aqm.getLn_smallParticle(),aqm.getLn_largeParticle()};
	    			analyticDAO.insertAQMData(insertargs);
	    		}
	    	}

	    }catch (Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			logger.debug("Exception in persistInputRawData : "+sw.toString() );
			
		}finally{
			try {
				 buffRead.close(); 
				is.close();
			} catch (IOException e) {
			}
		}


	}

}
