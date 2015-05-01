package com.example.dylosusbdatatransferapp.chart;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.example.dylosusbdatatransferapp.model.AQMResults;

public class AQMResultsGraph {
	
	private AQMResults aqmresult; 
	private static final String TAG = "AQMResultsGraph";
	public AQMResultsGraph(AQMResults aqmresult){
		this.aqmresult = aqmresult;
		
	}
	
	public Intent getIntent(Context context){
		Intent intent = null;
		try{
			String[] mean_Array = aqmresult.getMean_values().substring(1).split("\\|");
			int n_r = mean_Array.length;
			String devices[] = aqmresult.getComparison_type().substring(1).split("\\|");
			int n_b = devices.length;
			int n_w = n_r/n_b;
			int[] colors = {Color.RED,Color.BLUE};
			String[] types = new String[n_b];
			Log.d(TAG,"Imp values are : "+n_w+" :"+n_r+" : "+n_b);
			
			XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			XYMultipleSeriesRenderer mrenderer = new XYMultipleSeriesRenderer();
			

			int count = 0;
			for (int i = 0; i< devices.length; i++){
				XYSeries series = new XYSeries("");
				XYSeriesRenderer renderer = new XYSeriesRenderer();
				
				renderer.setColor(colors[i%2]);
				renderer.setLineWidth(3);
			    renderer.setDisplayChartValues(true);
				renderer.setFillPoints(true);
				renderer.setHighlighted(true);
				renderer.setPointStyle(PointStyle.CIRCLE);
				
				types[i] = org.achartengine.chart.CubicLineChart.TYPE;
				series.setTitle("Device " + devices[i]);
				for (int j=0; j<n_w; j++){
					Timestamp tstart = new Timestamp(aqmresult.getAnalysis_starttime().getTime());
					tstart.setTime((long) (tstart.getTime() + (0.25 + 0.5 * j)*3600*1000l));
					double minuteofday = tstart.getMinutes() + tstart.getHours()*60;
					String y_mean_val_str = mean_Array[count++];
					double y_mean_val = Double.valueOf(y_mean_val_str);	
					Log.d(TAG,"Imp values are of coordinates : "+y_mean_val+" :"+minuteofday+" : ");
					series.add(minuteofday, y_mean_val);

				}
				dataset.addSeries(series);
				mrenderer.addSeriesRenderer(renderer);
				series = null;
			}
			
			XYSeries seriesThreshold = new XYSeries("");
			seriesThreshold.add((aqmresult.getAnalysis_starttime().getMinutes() + aqmresult.getAnalysis_starttime().getHours()*60) , Math.log(15.4));
			seriesThreshold.add((aqmresult.getAnalysis_endtime().getMinutes() + aqmresult.getAnalysis_endtime().getHours()*60) , Math.log(15.4));
			seriesThreshold.setTitle("Good");
			dataset.addSeries(seriesThreshold);	
			XYSeriesRenderer rendererThreshold = new XYSeriesRenderer();
			rendererThreshold.setStroke(BasicStroke.DASHED);
			rendererThreshold.setColor(Color.GREEN);
			rendererThreshold.setLineWidth(3);
			rendererThreshold.setDisplayChartValues(true);
			rendererThreshold.setFillPoints(true);
			rendererThreshold.setHighlighted(true);
			rendererThreshold.setPointStyle(PointStyle.CIRCLE);
			mrenderer.addSeriesRenderer(rendererThreshold);
			
			XYSeries seriesThreshold1 = new XYSeries("");
			seriesThreshold1.add((aqmresult.getAnalysis_starttime().getMinutes() + aqmresult.getAnalysis_starttime().getHours()*60) , Math.log(35.4));
			seriesThreshold1.add((aqmresult.getAnalysis_endtime().getMinutes() + aqmresult.getAnalysis_endtime().getHours()*60) , Math.log(35.4));
			seriesThreshold1.setTitle("Moderate");
			dataset.addSeries(seriesThreshold1);	
			XYSeriesRenderer rendererThreshold1 = new XYSeriesRenderer();
			rendererThreshold1.setStroke(BasicStroke.DASHED);
			rendererThreshold1.setColor(Color.YELLOW);
			rendererThreshold1.setLineWidth(3);
			rendererThreshold1.setDisplayChartValues(true);
			rendererThreshold1.setFillPoints(true);
			rendererThreshold1.setHighlighted(true);
			rendererThreshold1.setPointStyle(PointStyle.CIRCLE);
			mrenderer.addSeriesRenderer(rendererThreshold1);
			
			XYSeries seriesThreshold2 = new XYSeries("");
			seriesThreshold2.add((aqmresult.getAnalysis_starttime().getMinutes() + aqmresult.getAnalysis_starttime().getHours()*60) , Math.log(65.4));
			seriesThreshold2.add((aqmresult.getAnalysis_endtime().getMinutes() + aqmresult.getAnalysis_endtime().getHours()*60) , Math.log(65.4));
			seriesThreshold2.setTitle("Unhealthy to Sensitive People");
			dataset.addSeries(seriesThreshold2);	
			XYSeriesRenderer rendererThreshold2 = new XYSeriesRenderer();
			rendererThreshold2.setStroke(BasicStroke.DASHED);
			rendererThreshold2.setColor(Color.parseColor("#FFA726"));
			rendererThreshold2.setLineWidth(3);
			rendererThreshold2.setDisplayChartValues(true);
			rendererThreshold2.setFillPoints(true);
			rendererThreshold2.setHighlighted(true);
			rendererThreshold2.setPointStyle(PointStyle.CIRCLE);
			mrenderer.addSeriesRenderer(rendererThreshold2);
			
			XYSeries seriesThreshold3 = new XYSeries("");
			seriesThreshold3.add((aqmresult.getAnalysis_starttime().getMinutes() + aqmresult.getAnalysis_starttime().getHours()*60) , Math.log(150.4));
			seriesThreshold3.add((aqmresult.getAnalysis_endtime().getMinutes() + aqmresult.getAnalysis_endtime().getHours()*60) , Math.log(150.4));
			seriesThreshold3.setTitle("Unhealthy to everyone");
			dataset.addSeries(seriesThreshold3);	
			XYSeriesRenderer rendererThreshold3 = new XYSeriesRenderer();
			rendererThreshold3.setStroke(BasicStroke.DASHED);
			rendererThreshold3.setColor(Color.RED);
			rendererThreshold3.setLineWidth(3);
			rendererThreshold3.setDisplayChartValues(true);
			rendererThreshold3.setFillPoints(true);
			rendererThreshold3.setHighlighted(true);
			rendererThreshold3.setPointStyle(PointStyle.CIRCLE);
			mrenderer.addSeriesRenderer(rendererThreshold3);
			
			
			
			mrenderer.setXTitle("MinuteofDay");
			mrenderer.setYTitle("ln(PM2.5 Concentration)");
			 mrenderer.setAxisTitleTextSize(16); // 16
			 mrenderer.setChartTitleTextSize(20); // 20
			    mrenderer.setLabelsTextSize(15); // 15
			    mrenderer.setLegendTextSize(15); // 15
			    mrenderer.setPointSize(0); // 10
			   // mrenderer.setMargins(new int[] { 20, 30, 15, 0 });      
			    mrenderer.setZoomButtonsVisible(true);
			    mrenderer.setShowAxes(true);
			    mrenderer.setShowLegend(true);
			    mrenderer.setShowGridX(true);
			    mrenderer.setShowLabels(true);
			    mrenderer.setYLabels(30);
			    mrenderer.setYAxisMin(-6.00);
			    mrenderer.setYAxisMax(6.00);
			
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			intent = ChartFactory.getLineChartIntent(context, 
					dataset, mrenderer,"AQM Data Results between "+sdf.format
					(aqmresult.getAnalysis_starttime()) + " and " + sdf.format
							(aqmresult.getAnalysis_endtime()));
			
			
		}
		catch (Exception e){
			Log.d("Graph - Error", e.getLocalizedMessage());
		}
		return intent;
	}
		

}
