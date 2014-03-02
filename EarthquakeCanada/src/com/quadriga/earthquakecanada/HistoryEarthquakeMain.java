package com.quadriga.earthquakecanada;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class HistoryEarthquakeMain extends SherlockFragmentActivity {

	private LinearLayout llProgress, llEmptyMessage;
	private static ListView latest_eqlist;
	private static HistoryEarthquakeAdapter eqAdapter;
	private static List<HistoryEarthquake> eqDetails;
	private EditText editsearch;
	private String provienceCode;
	private double magnitude;
	private String url;
	private ProgressDialog dialogEq;
	private int count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.earthquake_main);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		findViewById();
		count = 0;
		url = this.getIntent().getStringExtra("url").toString().trim();
		provienceCode = this.getIntent().getStringExtra("provience_code")
				.toString().trim();
		magnitude = Double.parseDouble(this.getIntent()
				.getStringExtra("magnitude_value").toString().trim());

		GetStart StartTaskObject = new GetStart();
		StartTaskObject.execute();

		latest_eqlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final HistoryEarthquake eq = (HistoryEarthquake) parent
						.getItemAtPosition(position);
				moreOptions(eq, position);
			}
		});
		
		

		latest_eqlist.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final HistoryEarthquake eq = (HistoryEarthquake) parent
						.getItemAtPosition(position);
				moreOptions(eq, position);
				return true;
			}
		});

	}

	public void moreOptions(final HistoryEarthquake eq, final int position) {

		final CharSequence[] items = { "View details", "View map" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(eq.getEqtitle());
		// builder.setIcon(mAdapterPhone.getIcons().get(app.getPackageNamePhone()));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].toString().equals("View details")) {
					Intent intent = new Intent(HistoryEarthquakeMain.this,
							SingleEarthquakeMain.class);
					intent.putExtra("full_title", eq.getEqfulltitle());
					intent.putExtra("datetime", eq.getEqorigintime());
					intent.putExtra("geolon", eq.getEqlon());
					intent.putExtra("geolat", eq.getEqlat());
					intent.putExtra("depth", eq.getEqdepth());
					intent.putExtra("magnitude", eq.getEqmagnitude() + " "
							+ eq.getEqMagnitudeType().toUpperCase());
					startActivity(intent);
				} else if (items[item].toString().equals("View map")) {
					Intent intent = new Intent(HistoryEarthquakeMain.this,
							EqGmap.class);
					intent.putExtra("full_title", eq.getEqfulltitle());
					intent.putExtra("datetime", eq.getEqorigintime());
					intent.putExtra("geolon", eq.getEqlon());
					intent.putExtra("geolat", eq.getEqlat());
					intent.putExtra("depth", eq.getEqdepth());
					intent.putExtra("magnitude", eq.getEqmagnitude() + " "
							+ eq.getEqMagnitudeType().toUpperCase());
					startActivity(intent);
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.action_menu, menu);

		editsearch = (EditText) menu.findItem(R.id.menu_search).getActionView();
		editsearch.addTextChangedListener(textWatcher);

		MenuItem menuSearch = menu.findItem(R.id.menu_search);
		menuSearch.setOnActionExpandListener(new OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				editsearch.setText("");
				editsearch.clearFocus();
				return true;
			}

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				editsearch.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				return true;
			}
		});
		return true;
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String text = editsearch.getText().toString()
					.toLowerCase(Locale.getDefault());
			try {
				eqAdapter.filter(text);
			} catch (Exception e) {

			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}

	};

	private String getSortMethodPref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(HistoryEarthquakeMain.this
						.getApplicationContext());
		String result = sharedPreferences.getString("sort_method", "datetime");
		return result;
	}

	private void saveSortMethodPref(String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(HistoryEarthquakeMain.this);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	private void loadOptionForSort() {
		final CharSequence[] items = { "Magnitude", "Provience",
				"Date and Time" };

		AlertDialog.Builder builder = new AlertDialog.Builder(
				HistoryEarthquakeMain.this);
		builder.setTitle("Sort by :");
		int iCurrent;
		String method = getSortMethodPref();
		if (method.equals("magnitude")) {
			iCurrent = 0;
		} else if (method.equals("provience")) {
			iCurrent = 1;
		} else if (method.equals("datetime")) {
			iCurrent = 2;
		} else {
			iCurrent = 0;
		}

		builder.setSingleChoiceItems(items, iCurrent,
				new DialogInterface.OnClickListener() {
					String sSortMethod = null;

					@SuppressWarnings("unused")
					public void onClick(DialogInterface dialog, int item) {
						if (items[item].toString().equals("Magnitude")) {
							dialog.dismiss();
							sSortMethod = "magnitude";
						} else if (items[item].toString().equals("Provience")) {
							dialog.dismiss();
							sSortMethod = "provience";
						} else if (items[item].toString().equals(
								"Date and Time")) {
							dialog.dismiss();
							sSortMethod = "datetime";
						} else {
							dialog.dismiss();
							sSortMethod = "datetime";
						}
						saveSortMethodPref("sort_method", sSortMethod);
						SortingTask SortTaskObject = new SortingTask();
						SortTaskObject.execute();
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	class SortingTask extends AsyncTask<Object, Void, String> {
		@Override
		public void onPreExecute() {
			super.onPreExecute();
			dialogEq = new ProgressDialog(HistoryEarthquakeMain.this);
			dialogEq.setTitle("Sorting...");
			dialogEq.setMessage("Please Wait...");
			dialogEq.setCancelable(true);
			dialogEq.show();
		}

		@Override
		public String doInBackground(Object... params) {
			doSort();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialogEq.dismiss();
			eqAdapter.notifyDataSetChanged();
		}
	}

	private void doSort() {
		HistoryEarthquakeComparator comparatorEq = new HistoryEarthquakeComparator(
				HistoryEarthquakeMain.this);
		Collections.sort(eqDetails, comparatorEq);
		// mAdapterPhone.notifyDataSetChanged();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals(getString(R.string.menu_settings))) {
			loadSettingsPref();
			return true;
		} else if (item.getTitle().equals(getString(R.string.menu_sort_by))) {
			loadOptionForSort();
			return true;
		} else if (item.getTitle().equals(getString(R.string.menu_refresh))) {
			doRefresh();
			return true;
		} else if (item.getItemId() == android.R.id.home) {
			this.finish();
			return true;
		}
		return false;
	}

	private void loadSettingsPref() {
		Intent intent = new Intent(HistoryEarthquakeMain.this, Settings.class);
		startActivityForResult(intent, 1);
	}

	public int getLatestDaysPref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(HistoryEarthquakeMain.this);
		int result = sharedPreferences.getInt("latest_days", 7);
		return result;
	}

	public void doRefresh() {
		count = 0;
		url = this.getIntent().getStringExtra("url").toString().trim();
		provienceCode = this.getIntent().getStringExtra("provience_code")
				.toString().trim();
		magnitude = Double.parseDouble(this.getIntent()
				.getStringExtra("magnitude_value").toString().trim());

		GetStart StartTaskObject = new GetStart();
		StartTaskObject.execute();
	}

	public void findViewById() {
		llProgress = (LinearLayout) findViewById(R.id.llProgress);
		latest_eqlist = (ListView) findViewById(R.id.latest_eqlist);
		llEmptyMessage = (LinearLayout) findViewById(R.id.llEmptyMessage);
		// latest_eqlist.setOnItemClickListener(this);
		// latest_eqlist.setOnItemLongClickListener(this);
	}

	class GetStart extends AsyncTask<Object, Void, String> {
		@Override
		public void onPreExecute() {
			super.onPreExecute();
			llProgress.setVisibility(View.VISIBLE);
			llEmptyMessage.setVisibility(View.GONE);
		}

		@Override
		public String doInBackground(Object... params) {
			doWork();
			doSort();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (count == 0) {
				llProgress.setVisibility(View.GONE);
				llEmptyMessage.setVisibility(View.VISIBLE);
			} else {
				llProgress.setVisibility(View.GONE);
				llEmptyMessage.setVisibility(View.GONE);
				eqAdapter = new HistoryEarthquakeAdapter(
						HistoryEarthquakeMain.this.getApplicationContext(),
						eqDetails);
				eqAdapter.setListItems(eqDetails);
				latest_eqlist.setAdapter(eqAdapter);
			}
		}
	}

	public void doWork() {
		eqDetails = loadEqDetails();
	}

	@SuppressWarnings({ "static-access" })
	private List<HistoryEarthquake> loadEqDetails() {
		List<HistoryEarthquake> earthquakes = new ArrayList<HistoryEarthquake>();

		try {
			XMLParser parser = new XMLParser();
			String xml = null;
			try {
				xml = parser.executeHttpGet(url);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// XML
			Document doc = parser.getDomElement(xml); // getting DOM element

			NodeList nodes = doc.getElementsByTagName("event");
			Log.e("Size: ", "" + nodes.getLength());
			for (int i = 1; i < nodes.getLength(); i++) {
				HistoryEarthquake eq = new HistoryEarthquake();
				// NodeList events = nodes.item(i).get getChildNodes();

				// Log.e("Sub Size: ", "" + events.getLength());

				Element e = (Element) nodes.item(i);
				double eqmagnitude = Double.parseDouble(parser.getValue(e,
						"magnitude").toString());
				if (eqmagnitude < magnitude)
					continue;
				String title[] = parser.getValue(e, "location").toString()
						.split(",");
				if (title.length > 1) {
					String codes[] = title[1].trim().split(" ");
					if (!provienceCode.equals("NA")
							&& !codes[0].trim().equals(provienceCode))
						continue;
					eq.setEqtitle(title[0].trim());
					eq.setEqprovience(codes[0].trim().toUpperCase());
				} else {
					if (!provienceCode.equals("OT"))
						continue;
					eq.setEqtitle(title[0].trim());
					eq.setEqprovience("OTHER");
				}
				eq.setEqmagnitude(String.format("%.1f",
						Double.parseDouble(parser.getValue(e, "magnitude"))));
				eq.setEqfulltitle(parser.getValue(e, "location").toString());
				eq.setEqlon(parser.getValue(e, "geo:lon").toString());
				eq.setEqlat(parser.getValue(e, "geo:lat").toString());
				eq.setEqorigintime(parser.getValue(e, "origin_time").toString()
						.replace("T", " ").replace("+0000", " (GMT)"));
				eq.setEqdepth(parser.getValue(e, "depth").toString());
				eq.setEqMagnitudeType(parser.getValue(e, "magnitude_type")
						.toString());
				count++;
				earthquakes.add(eq);
				// arraylist.add(eq);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return earthquakes;
	}

}
