package com.quadriga.earthquakecanada;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

public class EqTaskService extends IntentService {
	private ArrayList<HashMap<String, String>> mylist;
	private String strInternetConnected;

	public EqTaskService() {
		super("EqTaskService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0) {

		try {

			strInternetConnected = checkInternetConnection();
			if (strInternetConnected.equals("yes")) {
				checkForEarthquake();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public String checkInternetConnection() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected())
			return "yes";
		else
			return "no";
	}

	public void checkForEarthquake() {
		try {
			LatestEarthquake eq = new LatestEarthquake();

			XMLParser parser = new XMLParser();
			String xml = null;
			try {
				String url = "http://www.earthquakescanada.nrcan.gc.ca/api/earthquakes/latest/7d.xml";
				xml = parser.executeHttpGet(url);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// XML
			Document doc = parser.getDomElement(xml); // getting DOM element

			NodeList nodes = doc.getElementsByTagName("event");

			Element e = (Element) nodes.item(1);

			String title[] = parser.getValue(e, "location").toString()
					.split(",");

			if (title.length > 1) {
				String codes[] = title[1].trim().split(" ");
				eq.setEqtitle(Character.toUpperCase(title[0].charAt(0))
						+ title[0].substring(1).toLowerCase().trim());
				eq.setEqprovience(codes[0].trim().toUpperCase());
			} else {
				eq.setEqtitle(Character.toUpperCase(title[0].charAt(0))
						+ title[0].substring(1).toLowerCase().trim());
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

			showNotification(eq);
			/*
			 * if(!eq.getEqfulltitle().equals(getLatestEqDatetimePref())) {
			 * saveLatestEqDatetimePref("latest_eq_datetime",
			 * eq.getEqfulltitle()); if(!getNotificationPref().equals("NA")) {
			 * showNotification(eq); } }
			 */

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public String getNotificationPref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String result = sharedPreferences
				.getString("notification_status", "30");
		return result;
	}

	private void saveLatestEqDatetimePref(String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getLatestEqDatetimePref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String result = sharedPreferences.getString("latest_eq_datetime", "NA");
		return result;
	}

	@SuppressWarnings("deprecation")
	public void showNotification(LatestEarthquake eq) {
		String ns = Context.NOTIFICATION_SERVICE;
		final int HELLO_ID = 1;
		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(ns);
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = "Earthquake";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		Context context = this.getApplicationContext();
		CharSequence contentTitle = "New Earthquake Detected..";
		String sQ = eq.getEqmagnitude() + " " + eq.getEqtitle();
		CharSequence contentText = sQ;

		Intent intent = new Intent(this, SingleEarthquakeMain.class);
		intent.putExtra("full_title", eq.getEqfulltitle());
		intent.putExtra("datetime", eq.getEqorigintime());
		intent.putExtra("geolon", eq.getEqlon());
		intent.putExtra("geolat", eq.getEqlat());
		intent.putExtra("depth", eq.getEqdepth());
		intent.putExtra("magnitude", eq.getEqmagnitude() + " "
				+ eq.getEqMagnitudeType().toUpperCase());

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		mNotificationManager.cancel(HELLO_ID);
		mNotificationManager.notify(HELLO_ID, notification);
	}

}
