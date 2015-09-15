package com.example.jacksonparsingtransportationandroid;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modellingpackages.TransportationInfoList;

public class MainActivity extends Activity implements View.OnClickListener {
	Button btnSavetoDB;
	String localURL = "http://192.168.0.78/crossover/transportation/admin/jsondata";
	String serverURL = "http://demo.crossovernepal.com/transportation/admin/jsonData";
	URL jsonURL;
	TextView tvAddedDate;
	MySQLiteHelper myDbHelper;
	boolean isdatabasecreated;
	boolean isnetworkpresent;
	public static TransportationInfoList transportInfo;
	ProgressBar progressBar1;
	JSONArray bookmarks, category, company, district, language, node, route,
			route_node, service, type, transportation, schedule,
			transportation_details = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myDbHelper = new MySQLiteHelper(getApplicationContext());
		// transportInfo = new TransportationInfoList();
		btnSavetoDB = (Button) findViewById(R.id.btnSavetoDB);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);

		isdatabasecreated = myDbHelper.checkDataBase();
		isnetworkpresent = isOnline();
		// byte[] jsonData = Files
		// .readAllBytes(Paths.get("transportinfojson.txt"));

		btnSavetoDB.setOnClickListener(this);
		tvAddedDate = (TextView) findViewById(R.id.tvAddedDate);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnSavetoDB:
			if (/* isdatabasecreated && */isnetworkpresent)
				new DoNetworkOperations().execute();
			else {
				Toast.makeText(
						getApplicationContext(),
						"Check internet connection or may be database is already created",
						Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	public class DoNetworkOperations extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub

			MainActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					progressBar1.setVisibility(View.VISIBLE);
				}
			});
			String jsondata = null;
			try {
				jsonURL = new URL(localURL);

			} catch (Exception e) {
				// TODO: handle exception
			}
			ObjectMapper mapper = new ObjectMapper();
			// transportInfo = new TransportationInfoList();
			try {
				if (transportInfo == null) {
					transportInfo = mapper.readValue(jsonURL,
							TransportationInfoList.class);
				}

				// converting POJO back to json
				/*
				 * jsondata = mapper.writerWithDefaultPrettyPrinter()
				 * .writeValueAsString(transportInfo);
				 */
				jsondata = mapper.writeValueAsString(transportInfo);

				Log.d("crossover", "POJO converted back to json" + jsondata);
				if (jsondata != null) {

					try {

						JSONObject jsonObj = new JSONObject(jsondata);
						bookmarks = jsonObj
								.getJSONArray(myDbHelper.TAG_BOOKMARKS);
						category = jsonObj
								.getJSONArray(myDbHelper.TAG_CATEGORY);
						company = jsonObj.getJSONArray(myDbHelper.TAG_COMPANY);
						district = jsonObj
								.getJSONArray(myDbHelper.TAG_DISTRICT);
						language = jsonObj
								.getJSONArray(myDbHelper.TAG_LANGUAGE);
						node = jsonObj.getJSONArray(myDbHelper.TAG_NODE);
						route = jsonObj.getJSONArray(myDbHelper.TAG_ROUTE);
						route_node = jsonObj
								.getJSONArray(myDbHelper.TAG_ROUTE_NODE);
						service = jsonObj.getJSONArray(myDbHelper.TAG_SERVICE);
						type = jsonObj.getJSONArray(myDbHelper.TAG_TYPE);
						transportation = jsonObj
								.getJSONArray(myDbHelper.TAG_TRANSPORTATION);
						schedule = jsonObj
								.getJSONArray(myDbHelper.TAG_SCHEDULE);
						transportation_details = jsonObj
								.getJSONArray(myDbHelper.TAG_TRANSPORTATION_DETAILS);

						myDbHelper = new MySQLiteHelper(getApplicationContext());

						myDbHelper.createInsertQuery(myDbHelper.TAG_BOOKMARKS,
								bookmarks);
						myDbHelper.createInsertQuery(myDbHelper.TAG_CATEGORY,
								category);
						myDbHelper.createInsertQuery(myDbHelper.TAG_COMPANY,
								company);
						myDbHelper.createInsertQuery(myDbHelper.TAG_DISTRICT,
								district);
						myDbHelper.createInsertQuery(myDbHelper.TAG_LANGUAGE,
								language);
						myDbHelper.createInsertQuery(myDbHelper.TAG_NODE, node);
						myDbHelper.createInsertQuery(myDbHelper.TAG_ROUTE,
								route);
						myDbHelper.createInsertQuery(myDbHelper.TAG_ROUTE_NODE,
								route_node);
						myDbHelper.createInsertQuery(myDbHelper.TAG_SERVICE,
								service);
						myDbHelper.createInsertQuery(myDbHelper.TAG_TYPE, type);
						myDbHelper.createInsertQuery(myDbHelper.TAG_SCHEDULE,
								schedule);
						myDbHelper.createInsertQuery(
								myDbHelper.TAG_TRANSPORTATION_DETAILS,
								transportation_details);
						myDbHelper.createInsertQuery(
								myDbHelper.TAG_TRANSPORTATION, transportation);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						// showAlert("Could Not Connect To Server.", "Oops!!");

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {

				}

			} catch (JsonProcessingException e) {
				// TODO: handle exception
				Log.d("crossover", "JsonProcessingException caught");
				System.out.println("JsonProcessingException caught");

			} catch (IOException e) {
				// TODO: handle exception
				Log.d("crossover", "IOException caught");
				System.out.println("IOException caught");
			}

			// check if all datas are converted into POJO
			String added_date_piece = transportInfo.getTransportation_details()
					.get(3).getAdded_date();

			Log.d("crossover", "some random added date is:" + added_date_piece);

			Log.d("crossover", "converting back to json" + jsondata);

			return added_date_piece;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			MainActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					progressBar1.setVisibility(View.GONE);
				}
			});
			Log.d("crossover", "some random added date is:" + result);
			tvAddedDate.setText(result);
		}
	}

	private boolean isOnline() {
		// TODO Auto-generated method stub
		ConnectivityManager cm = (ConnectivityManager) MainActivity.this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting();

	}
}
