package com.quadriga.earthquakecanada;

import java.util.Calendar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.quadriga.earthquakecanada.LatestEarthquakeMain.SortingTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class HistoryActivity extends SherlockFragment {

	private Button btnSearch;
	private Spinner provience, spYear, month, magnitude;
	private String url;
	private String provienceCode;
	private String mag;
	private String yearValues[];

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.history, container, false);

		findViewById(rootView);

		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getHistoryUrl();
			}
		});
		return rootView;
	}

	public void getHistoryUrl() {
		String provienceValues[] = getResources().getStringArray(
				R.array.provience_values);
		String monthValues[] = getResources().getStringArray(
				R.array.month_values);
		String magnitudeValues[] = getResources().getStringArray(
				R.array.magnitude_values);

		if (monthValues[month.getSelectedItemPosition()].equals("NA")) {
			url = getResources().getString(R.string.urlbydate)
					+ spYear.getSelectedItem().toString() + ".xml";
		} else {
			url = getResources().getString(R.string.urlbydate)
					+ spYear.getSelectedItem().toString() + "-"
					+ monthValues[month.getSelectedItemPosition()] + ".xml";
		}
		provienceCode = provienceValues[provience.getSelectedItemPosition()];
		mag = magnitudeValues[magnitude.getSelectedItemPosition()];

		Intent intent = new Intent(this.getActivity(),
				HistoryEarthquakeMain.class);
		intent.putExtra("url", url);
		intent.putExtra("provience_code", provienceCode);
		intent.putExtra("magnitude_value", mag);
		startActivity(intent);
	}
	
	public void findViewById(View rootView) {
		setHasOptionsMenu(true);
		btnSearch = (Button) rootView.findViewById(R.id.btnSearch);
		provience = (Spinner) rootView.findViewById(R.id.provience);
		spYear = (Spinner) rootView.findViewById(R.id.spYear);
		month = (Spinner) rootView.findViewById(R.id.month);
		magnitude = (Spinner) rootView.findViewById(R.id.magnitude);
		// latest_eqlist.setOnItemClickListener(this);
		// latest_eqlist.setOnItemLongClickListener(this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		// super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.action_menu, menu);

		MenuItem menuSearch = menu.findItem(R.id.menu_search);
		MenuItem menuRefresh = menu.findItem(R.id.menu_refresh);
		menuSearch.setVisible(false);
		menuRefresh.setVisible(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals(getString(R.string.menu_settings))) {
			loadSettingsPref();
			return true;
		} else if (item.getTitle().equals(getString(R.string.menu_sort_by))) {
			loadOptionForSort();
			return true;
		}
		return false;
	}

	private void loadSettingsPref() {
		Intent intent = new Intent(getActivity(), Settings.class);
		startActivityForResult(intent, 1);
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
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void saveSortMethodPref(String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	private String getSortMethodPref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());
		String result = sharedPreferences.getString("sort_method", "datetime");
		return result;
	}
}
