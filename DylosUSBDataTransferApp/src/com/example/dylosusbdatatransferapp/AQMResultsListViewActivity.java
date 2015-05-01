package com.example.dylosusbdatatransferapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

import com.example.dylosusbdatatransferapp.chart.AQMResultsGraph;
import com.example.dylosusbdatatransferapp.dao.AQMResultsDBHandler;
import com.example.dylosusbdatatransferapp.model.AQMResults;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AQMResultsListViewActivity extends ListActivity {
	private static final String TAG = "AQMResultsListViewActivity";
	private AQMResultsDBHandler dbHandler = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dbHandler = new AQMResultsDBHandler(this);
		String body = "{\"listofqmresult\":[{\"analysis_endtime\":\"2015-04-16T17:41:50\",\"analysis_starttime\":\"2015-04-16T16:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":66,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.273072|0.335704|0.30320400000000003|0.3841\",\"predicted_values\":\"|0.7959844258051239|0.7341569597584698\"},{\"analysis_endtime\":\"2015-04-16T16:41:50\",\"analysis_starttime\":\"2015-04-16T15:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":65,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.318824|0.236096|0.434168|0.335116\",\"predicted_values\":\"|0.7831013677817706|0.7346310831810322\"},{\"analysis_endtime\":\"2015-04-16T15:41:50\",\"analysis_starttime\":\"2015-04-16T14:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":64,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.273072|0.335704|0.30320400000000003|0.3841\",\"predicted_values\":\"|0.7702183097584175|0.7351052066035947\"},{\"analysis_endtime\":\"2015-04-16T14:41:50\",\"analysis_starttime\":\"2015-04-16T13:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":63,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.501352|0.380924|0.477824|0.371272\",\"predicted_values\":\"|0.735579330026157|0.7573352517350643\"},{\"analysis_endtime\":\"2015-04-16T13:41:50\",\"analysis_starttime\":\"2015-04-16T12:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":62,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.661408|0.584432|0.643368|0.592344\",\"predicted_values\":\"|0.7444521937117112|0.7360534534487194\"},{\"analysis_endtime\":\"2015-04-16T12:41:50\",\"analysis_starttime\":\"2015-04-16T11:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":61,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.928336|0.78156|0.79352|0.695416\",\"predicted_values\":\"|0.731569135688358|0.7365275768712818\"},{\"analysis_endtime\":\"2015-04-16T11:41:50\",\"analysis_starttime\":\"2015-04-16T10:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":60,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|1.084428|0.954116|1.200664|1.073748\",\"predicted_values\":\"|0.7370017002938443|0.7186860776650048\"},{\"analysis_endtime\":\"2015-04-16T10:41:50\",\"analysis_starttime\":\"2015-04-16T09:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":59,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|1.320832|1.19894|1.434048|1.306616\",\"predicted_values\":\"|0.7374758237164066|0.7058030196416517\"},{\"analysis_endtime\":\"2015-04-16T09:41:50\",\"analysis_starttime\":\"2015-04-16T08:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":58,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|2.112168|1.585576|1.613428|1.548724\",\"predicted_values\":\"|0.737949947138969|0.6929199616182985\"},{\"analysis_endtime\":\"2015-04-16T08:41:50\",\"analysis_starttime\":\"2015-04-16T07:41:50\",\"badness_indicators\":\"|false|false|true|true\",\"comparison_type\":null,\"id\":57,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|1.213|1.405244|3.692368|3.21732\",\"predicted_values\":\"|0.6800369035949453|0.7384240705615314\"},{\"analysis_endtime\":\"2015-04-16T07:41:50\",\"analysis_starttime\":\"2015-04-16T06:41:50\",\"badness_indicators\":\"|false|false|true|true\",\"comparison_type\":null,\"id\":56,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.5554640000000001|1.011468|2.785412|3.687444\",\"predicted_values\":\"|0.6671538455715922|0.7388981939840938\"},{\"analysis_endtime\":\"2015-04-16T06:41:50\",\"analysis_starttime\":\"2015-04-16T05:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":55,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.23164400000000002|0.19357200000000002|-0.373524|1.337464\",\"predicted_values\":\"|0.654270787548239|0.7393723174066562\"},{\"analysis_endtime\":\"2015-04-16T05:41:50\",\"analysis_starttime\":\"2015-04-16T04:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":54,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.58434|0.364472|-0.352056|-0.388712\",\"predicted_values\":\"|0.6413877295248858|0.7398464408292186\"},{\"analysis_endtime\":\"2015-04-16T04:41:50\",\"analysis_starttime\":\"2015-04-16T03:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":53,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|-0.35584|-0.330924|1.092748|0.79668\",\"predicted_values\":\"|0.740320564251781|0.6285046715015327\"},{\"analysis_endtime\":\"2015-04-16T03:41:50\",\"analysis_starttime\":\"2015-04-16T02:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":52,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|-0.372872|-0.38598|0.343752|0.8580760000000001\",\"predicted_values\":\"|0.7407946876743434|0.6156216134781795\"},{\"analysis_endtime\":\"2015-04-16T02:41:50\",\"analysis_starttime\":\"2015-04-16T01:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":51,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.016451999999999998|-0.041952|-0.092908|-0.243716\",\"predicted_values\":\"|0.6027385554548264|0.7412688110969058\"},{\"analysis_endtime\":\"2015-04-16T01:41:50\",\"analysis_starttime\":\"2015-04-16T00:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":50,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.16746|0.003515999999999997|0.25502400000000003|0.138264\",\"predicted_values\":\"|0.7417429345194682|0.5898554974314731\"},{\"analysis_endtime\":\"2015-04-16T00:41:50\",\"analysis_starttime\":\"2015-04-15T23:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":49,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.219992|0.1741|0.479572|0.357952\",\"predicted_values\":\"|0.7422170579420306|0.5769724394081199\"},{\"analysis_endtime\":\"2015-04-15T23:41:50\",\"analysis_starttime\":\"2015-04-15T22:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":48,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.692316|0.5835319999999999|0.501264|0.37684\",\"predicted_values\":\"|0.8732827739452429|0.7313122192230955\"},{\"analysis_endtime\":\"2015-04-15T22:41:50\",\"analysis_starttime\":\"2015-04-15T21:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":47,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.810984|0.79662|0.922196|0.701864\",\"predicted_values\":\"|0.8603997159218897|0.7317863426456579\"},{\"analysis_endtime\":\"2015-04-15T21:41:50\",\"analysis_starttime\":\"2015-04-15T20:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":46,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.728764|0.705912|0.805196|0.733504\",\"predicted_values\":\"|0.7322604660682203|0.8475166578985365\"},{\"analysis_endtime\":\"2015-04-15T20:41:50\",\"analysis_starttime\":\"2015-04-15T19:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":45,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.626808|0.906748|0.635048|0.784684\",\"predicted_values\":\"|0.7327345894907826|0.8346335998751834\"},{\"analysis_endtime\":\"2015-04-15T19:41:50\",\"analysis_starttime\":\"2015-04-15T18:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":44,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|1.004456|0.87236|0.759648|0.764704\",\"predicted_values\":\"|0.7332087129133451|0.8217505418518303\"},{\"analysis_endtime\":\"2015-04-15T18:41:50\",\"analysis_starttime\":\"2015-04-15T17:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":43,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.489432|0.48643600000000004|0.466208|0.569732\",\"predicted_values\":\"|0.8088674838284771|0.7336828363359075\"},{\"analysis_endtime\":\"2015-04-15T17:41:50\",\"analysis_starttime\":\"2015-04-15T16:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":42,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.430724|0.436676|0.5714319999999999|0.510228\",\"predicted_values\":\"|0.7341569597584698|0.7959844258051239\"},{\"analysis_endtime\":\"2015-04-15T16:41:50\",\"analysis_starttime\":\"2015-04-15T15:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":41,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.8162159999999999|0.62132|0.956336|0.594496\",\"predicted_values\":\"|0.7346310831810322|0.7831013677817706\"},{\"analysis_endtime\":\"2015-04-15T15:41:50\",\"analysis_starttime\":\"2015-04-15T14:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":40,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|1.048624|0.984932|1.0101040000000001|0.917364\",\"predicted_values\":\"|0.7702183097584175|0.7351052066035947\"},{\"analysis_endtime\":\"2015-04-15T14:41:50\",\"analysis_starttime\":\"2015-04-15T13:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":39,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|0.97956|1.00874|1.242468|1.123772\",\"predicted_values\":\"|0.7573352517350643|0.735579330026157\"},{\"analysis_endtime\":\"2015-04-15T13:41:50\",\"analysis_starttime\":\"2015-04-15T12:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":38,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|1.564344|1.39702|1.088492|0.99926\",\"predicted_values\":\"|0.7360534534487194|0.7444521937117112\"},{\"analysis_endtime\":\"2015-04-15T12:41:50\",\"analysis_starttime\":\"2015-04-15T11:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":37,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|1.963232|1.74582|1.2620959999999999|1.208396\",\"predicted_values\":\"|0.7365275768712818|0.731569135688358\"},{\"analysis_endtime\":\"2015-04-15T11:41:50\",\"analysis_starttime\":\"2015-04-15T10:41:50\",\"badness_indicators\":\"|false|false|false|false\",\"comparison_type\":null,\"id\":36,\"is_btw_grp_significant\":false,\"is_interaction_significant\":true,\"is_lineartrend_significant\":true,\"is_within_grp_significant\":false,\"mean_values\":\"|1.34776|1.308008|2.364652|2.178136\",\"predicted_values\":\"|0.7186860776650048|0.7370017002938443\"}]}";
		body = body.replaceAll("null", "\"|aspira2|aspira4\"");
		int beginIndex = body.indexOf("[");
		int lastIndex = body.lastIndexOf("]");
		String actualBody = body.substring(beginIndex, lastIndex+1);
		actualBody = actualBody.replaceAll("T", " ");
		
		
	    GsonBuilder gsonBuilder = new GsonBuilder();
	   
	    gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    AQMResults[] arrayOfAqmResults = gson.fromJson(actualBody, AQMResults[].class);
	    
	    
		//List<AQMResults> listOfAqmResults = Arrays.asList(arrayOfAqmResults);
		
	    List<AQMResults> listOfAqmResults = dbHandler.Get_AQMResults(); 
		setListAdapter(new MyListAdaptor(this, listOfAqmResults));

		ListView lv = getListView();
		lv.setFastScrollEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
	

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				Log.d(TAG,"Value of onItemClick position is "+position);
				Log.d(TAG,"Value of onItemClick aqmresults obj mean values are is "+((AQMResults)getListAdapter().getItem(position)).getMean_values());
				AQMResultsGraph aqmGraph = new AQMResultsGraph(((AQMResults)getListAdapter().getItem(position)));
				Intent intent = aqmGraph.getIntent(getApplicationContext());
				startActivity(intent);
				//Toast.makeText(getApplicationContext(),((TextView) view).getText(), Toast.LENGTH_SHORT).show();
				
			}
		});
	}
	
	
	/**
	 * The List row creator
	 */
	class MyListAdaptor extends ArrayAdapter<AQMResults> implements SectionIndexer {

		HashMap<Integer, Integer> alphaIndexer;
		Integer[] sections;
		private View v;

		public MyListAdaptor(Context context, List<AQMResults> items) {
			super(context, R.layout.list, items);

			alphaIndexer = new HashMap<Integer, Integer>();
			int size = items.size();
			
			List<Integer> sectionList = new ArrayList<Integer>();
			
			for (int x = 0; x < size; x++) {
				AQMResults s = items.get(x);
				int unique_num = s.getId();
				sectionList.add(x);
				if (!alphaIndexer.containsKey(x))
					alphaIndexer.put(x,unique_num);
			}

			
			// create a list from the set to sort
			
			sections = new Integer[sectionList.size()];
			sections = sectionList.toArray(sections);
		}

		@Override
		public int getPositionForSection(int section) {
			return alphaIndexer.get(sections[section]);
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		@Override
		public Object[] getSections() {
			return sections;
		}
		
		
	}


}
