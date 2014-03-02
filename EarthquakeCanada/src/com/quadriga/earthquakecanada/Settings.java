package com.quadriga.earthquakecanada;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class Settings extends SherlockPreferenceActivity {

	public static String status = "no";

	public int getLatestDaysPref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		int result = sharedPreferences.getInt("latest_days", 7);
		return result;
	}

	private void saveLatestDaysPref(String key, int value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public String getSortTypePref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String result = sharedPreferences.getString("sort_type", "AESC");
		return result;
	}

	private void saveSortTypePref(String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getDistancePref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String result = sharedPreferences.getString("distance_miles", "NA");
		return result;
	}

	private void saveDistancePref(String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public float getMagnitudePref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		float result = sharedPreferences.getFloat("magnitude_level", 1.0f);
		return result;
	}

	private void saveMagnitudePref(String key, float value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	public String getNotificationPref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String result = sharedPreferences
				.getString("notification_status", "30");
		return result;
	}

	private void saveNotificationPref(String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Preference aboutPref = (Preference) findPreference("aboutPref");
		aboutPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(Settings.this, About.class));
				SharedPreferences customSharedPreference = getSharedPreferences(
						"myCustomSharedPrefs", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = customSharedPreference.edit();
				editor.putString("myCustomPref",
						"The preference has been clicked");
				editor.commit();
				return true;
			}
		});

		status = "no";
		final ListPreference listLatestPref = (ListPreference) findPreference("listLatestPref");
		listLatestPref.setValue(String.valueOf(getLatestDaysPref()));
		listLatestPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						listLatestPref.setValue(String
								.valueOf(getLatestDaysPref()));
						return false;
					}
				});
		listLatestPref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						status = "yes";
						saveLatestDaysPref("latest_days",
								Integer.valueOf(newValue.toString()));
						((ListPreference) preference).setValue(newValue
								.toString());
						return false;
					}
				});

		final ListPreference listMagnitudePref = (ListPreference) findPreference("listMagnitudePref");
		listMagnitudePref.setValue(String.valueOf(getMagnitudePref()));
		listMagnitudePref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						listMagnitudePref.setValue(String
								.valueOf(getMagnitudePref()));
						return false;
					}
				});
		listMagnitudePref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						status = "yes";
						saveMagnitudePref("magnitude_level",
								Float.valueOf(newValue.toString()));
						((ListPreference) preference).setValue(newValue
								.toString());
						return false;
					}
				});

		final ListPreference listNotificationPref = (ListPreference) findPreference("listNotificationPref");
		listNotificationPref.setValue(getNotificationPref());
		listNotificationPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						listNotificationPref.setValue(getNotificationPref());
						return false;
					}
				});
		listNotificationPref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						status = "yes";
						saveNotificationPref("notification_status",
								newValue.toString());

						setNotification(newValue.toString());

						((ListPreference) preference).setValue(newValue
								.toString());
						return false;
					}
				});

		final ListPreference listSortTypePref = (ListPreference) findPreference("listSortTypePref");
		listSortTypePref.setValue(getSortTypePref());
		listSortTypePref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						listSortTypePref.setValue(getSortTypePref());
						return false;
					}
				});
		listSortTypePref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						status = "yes";
						saveSortTypePref("sort_type", newValue.toString());
						((ListPreference) preference).setValue(newValue
								.toString());
						return false;
					}
				});

		final ListPreference listDistancePref = (ListPreference) findPreference("listDistancePref");
		listDistancePref.setValue(getDistancePref());
		listDistancePref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						listDistancePref.setValue(getDistancePref());
						return false;
					}
				});
		listDistancePref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						status = "yes";
						saveDistancePref("distance_miles", newValue.toString());
						((ListPreference) preference).setValue(newValue
								.toString());
						return false;
					}
				});

	}

	public void setNotification(String value) {
		if (value.equals("NA")) {

		} else {
			cancelSchedulesOnly();
			startSchedule(value);
			Toast.makeText(
					Settings.this,
					"Notifacation Enabled @ interval of " + value + " minutes!",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void cancelSchedulesOnly() {
		Intent intent = new Intent(getApplicationContext(),
				NotificationReceiver.class);
		intent.putExtra(NotificationReceiver.ACTION_ALARM,
				NotificationReceiver.ACTION_ALARM);
		final PendingIntent pIntent = PendingIntent.getBroadcast(this, 1234567,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarms = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		alarms.cancel(pIntent);
	}

	public void cancelSchedules() {
		saveNotificationPref("notification_status", "NA");

		Intent intent = new Intent(getApplicationContext(),
				NotificationReceiver.class);
		intent.putExtra(NotificationReceiver.ACTION_ALARM,
				NotificationReceiver.ACTION_ALARM);
		final PendingIntent pIntent = PendingIntent.getBroadcast(this, 1234567,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarms = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		alarms.cancel(pIntent);
	}

	public void startSchedule(String sTime) {
		try {
			AlarmManager alarms = (AlarmManager) this
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(getApplicationContext(),
					NotificationReceiver.class);
			intent.putExtra(NotificationReceiver.ACTION_ALARM,
					NotificationReceiver.ACTION_ALARM);
			final PendingIntent pIntent = PendingIntent.getBroadcast(this,
					1234567, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			alarms.setRepeating(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis(),
					(Integer.valueOf(sTime) * 60 * 1000), pIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// do your own thing here
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
