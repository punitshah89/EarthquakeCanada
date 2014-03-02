package com.quadriga.earthquakecanada;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class LatestEarthquakeMain extends SherlockFragment {

	private LinearLayout llProgress, llEmptyMessage;
	private static ListView latest_eqlist;
	private static LatestEarthquakeAdapter eqAdapter;
	private static List<LatestEarthquake> eqDetails;
	private EditText editsearch;
	private String url;
	private ProgressDialog dialogEq;
	private int count;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.earthquake_main, container,
				false);

		findViewById(rootView);
		count = 0;
		if (getLatestDaysPref() == 7)
			url = getResources().getString(R.string.urlsevendays);
		else if (getLatestDaysPref() == 30)
			url = getResources().getString(R.string.urlonemonth);
		else if (getLatestDaysPref() == 365)
			url = getResources().getString(R.string.urloneyear);
		else
			url = getResources().getString(R.string.urlsevendays);
		GetStart StartTaskObject = new GetStart();
		StartTaskObject.execute();

		latest_eqlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final LatestEarthquake eq = (LatestEarthquake) parent
						.getItemAtPosition(position);
				moreOptions(eq, position);
			}
		});

		latest_eqlist.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final LatestEarthquake eq = (LatestEarthquake) parent
						.getItemAtPosition(position);
				moreOptions(eq, position);
				return true;
			}
		});

		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (Settings.status.equals("yes")) {
				doRefresh();
			}
		}
	}

	public void doRefresh() {
		count = 0;
		if (getLatestDaysPref() == 7)
			url = getResources().getString(R.string.urlsevendays);
		else if (getLatestDaysPref() == 30)
			url = getResources().getString(R.string.urlonemonth);
		else if (getLatestDaysPref() == 365)
			url = getResources().getString(R.string.urloneyear);
		else
			url = getResources().getString(R.string.urlsevendays);
		GetStart StartTaskObject = new GetStart();
		StartTaskObject.execute();
	}

	public void moreOptions(final LatestEarthquake eq, final int position) {

		final CharSequence[] items = { "View details", "View map" };
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle(eq.getEqtitle());
		// builder.setIcon(mAdapterPhone.getIcons().get(app.getPackageNamePhone()));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].toString().equals("View details")) {
					Intent intent = new Intent(getActivity(),
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
					Intent intent = new Intent(getActivity(),
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		// super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.action_menu, menu);
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
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				return true;
			}
		});
	}

	private String getSortMethodPref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());
		String result = sharedPreferences.getString("sort_method", "datetime");
		return result;
	}

	private void saveSortMethodPref(String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	private void loadOptionForSort() {
		final CharSequence[] items = { "Magnitude", "Provience",
				"Date and Time" };

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
		}
		return false;
	}

	private void loadSettingsPref() {
		Intent intent = new Intent(getActivity(), Settings.class);
		startActivityForResult(intent, 1);
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			try {
				String text = editsearch.getText().toString()
						.toLowerCase(Locale.getDefault());
				eqAdapter.filter(text);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
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

	public void findViewById(View rootView) {
		setHasOptionsMenu(true);
		llProgress = (LinearLayout) rootView.findViewById(R.id.llProgress);
		latest_eqlist = (ListView) rootView.findViewById(R.id.latest_eqlist);
		llEmptyMessage = (LinearLayout) rootView
				.findViewById(R.id.llEmptyMessage);
		// latest_eqlist.setOnItemClickListener(this);
		// latest_eqlist.setOnItemLongClickListener(this);
	}

	private void doSort() {
		EarthquakeComparator comparatorEq = new EarthquakeComparator(
				getActivity());
		Collections.sort(eqDetails, comparatorEq);
		// mAdapterPhone.notifyDataSetChanged();
	}

	class SortingTask extends AsyncTask<Object, Void, String> {
		@Override
		public void onPreExecute() {
			super.onPreExecute();
			dialogEq = new ProgressDialog(getActivity());
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
				eqAdapter = new LatestEarthquakeAdapter(getActivity()
						.getApplicationContext(), eqDetails);
				eqAdapter.setListItems(eqDetails);
				latest_eqlist.setAdapter(eqAdapter);
			}
		}
	}

	public void doWork() {
		try {
			if (eqDetails != null) {
				eqDetails.removeAll(eqDetails);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		eqDetails = loadEqDetails();
	}

	public float getMagnitudePref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		float result = sharedPreferences.getFloat("magnitude_level", 1.0f);
		return result;
	}

	@SuppressLint("DefaultLocale")
	@SuppressWarnings({ "static-access" })
	private List<LatestEarthquake> loadEqDetails() {
		List<LatestEarthquake> earthquakes = new ArrayList<LatestEarthquake>();

		XMLParser parser = new XMLParser();
		String xml = null;
		try {
			xml = parser.executeHttpGet(url);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			// e1.printStackTrace();
			e1.printStackTrace();
		}
		// XML
		Document doc = parser.getDomElement(xml); // getting DOM element

		NodeList nodes = doc.getElementsByTagName("event");

		for (int i = 1; i < nodes.getLength(); i++) {
			LatestEarthquake eq = new LatestEarthquake();
			// NodeList events = nodes.item(i).get getChildNodes();

			// Log.e("Sub Size: ", "" + events.getLength());

			Element e = (Element) nodes.item(i);

			if (Float.valueOf(parser.getValue(e, "magnitude")) < getMagnitudePref())
				continue;

			double distance = 0;

			try {
				distance = getDistance(Double.parseDouble(parser.getValue(e,
						"geo:lon").toString()), Double.parseDouble(parser
						.getValue(e, "geo:lat").toString()));
			} catch (Exception e2) {
				continue;
			}

			if (!getDistancePref().equals("NA")
					&& distance > Double.parseDouble(getDistancePref())) {
				continue;
			}

			String title[] = parser.getValue(e, "location").toString()
					.split(",");

			if (title.length > 1) {
				String codes[] = title[1].trim().split(" ");
				eq.setEqtitle(title[0].trim());
				eq.setEqprovience(codes[0].trim().toUpperCase());
			} else {
				eq.setEqtitle(title[0].trim());
				eq.setEqprovience("OTHER");
			}

			eq.setEqmagnitude(String.format("%.1f",
					Double.parseDouble(parser.getValue(e, "magnitude"))));
			eq.setEqfulltitle(parser.getValue(e, "location").toString());

			if (i == 1)
				saveLatestEqDatetimePref("latest_eq_datetime",
						eq.getEqfulltitle());

			eq.setEqlon(parser.getValue(e, "geo:lon").toString());
			eq.setEqlat(parser.getValue(e, "geo:lat").toString());
			eq.setEqorigintime(parser.getValue(e, "origin_time").toString()
					.replace("T", " ").replace("+0000", " (GMT)"));
			eq.setEqdepth(parser.getValue(e, "depth").toString());
			eq.setEqMagnitudeType(parser.getValue(e, "magnitude_type")
					.toString());

			// eq.setEqtitle(e.getAttribute("));
			// Log.e("Detail: ", eq.getEqtitle());
			earthquakes.add(eq);
			count++;
			// arraylist.add(eq);
		}
		return earthquakes;

	}

	public String getDistancePref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		String result = sharedPreferences.getString("distance_miles", "NA");
		return result;
	}

	public double getDistance(double lon, double lat) {
		GPSTracker gps = new GPSTracker(getActivity());
		double distance = 0;
		if (gps.canGetLocation()) {

			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();
			Location locationA = new Location("point A");
			locationA.setLatitude(latitude);
			locationA.setLongitude(longitude);
			Location locationB = new Location("point B");
			locationB.setLatitude(lat);
			locationB.setLongitude(lon);
			distance = (locationA.distanceTo(locationB) / 1000) * 0.621371;

		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			distance = -1;
			gps.showSettingsAlert();
		}
		return distance;
	}

	private void saveLatestEqDatetimePref(String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public int getLatestDaysPref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		int result = sharedPreferences.getInt("latest_days", 7);
		return result;
	}

}
