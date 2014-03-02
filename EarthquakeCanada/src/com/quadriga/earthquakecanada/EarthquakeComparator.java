package com.quadriga.earthquakecanada;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class EarthquakeComparator implements Comparator<LatestEarthquake> {

	private static Context ctx;

	@SuppressWarnings("static-access")
	public EarthquakeComparator(Context context) {
		this.ctx = context;
	}

	private String getSortMethodPref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String result = sharedPreferences.getString("sort_method", "datetime");
		return result;
	}

	private String getSortTypePref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String result = sharedPreferences.getString("sort_type", "AESC");
		return result;
	}

	@Override
	public int compare(LatestEarthquake movie1, LatestEarthquake movie2) {
		String rank1 = null;
		String rank2 = null;

		if (getSortMethodPref().equals("provience")) {
			rank1 = movie1.getEqprovience();
			rank2 = movie2.getEqprovience();

			if (rank1.compareToIgnoreCase(rank2) > 0) {
				if (getSortTypePref().equals("AESC"))
					return +1;
				else
					return -1;

			} else if (rank1.compareToIgnoreCase(rank2) < 0) {
				if (getSortTypePref().equals("AESC"))
					return -1;
				else
					return +1;
			} else {
				return 0;
			}
		} else if (getSortMethodPref().equals("datetime")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.CANADA);
			sdf.setTimeZone(TimeZone.getDefault());
			Date dt1 = null;
			Date dt2 = null;

			try {
				dt1 = sdf.parse(movie1.getEqorigintime());
				dt2 = sdf.parse(movie2.getEqorigintime());
			} catch (ParseException e) { //
				// TODO Auto-generated catch block e.printStackTrace(); }
			}

			int hours = (int) ((dt1.getTime() - dt2.getTime()) / (60 * 60 * 1000));

			if (hours > 0) {
				if (getSortTypePref().equals("AESC"))
					return +1;
				else
					return -1;

			} else if (hours < 0) {
				if (getSortTypePref().equals("AESC"))
					return -1;
				else
					return +1;
			} else
				return 0;

		} else if (getSortMethodPref().equals("magnitude")) {
			double mag1 = Double.parseDouble(movie1.getEqmagnitude());
			double mag2 = Double.parseDouble(movie2.getEqmagnitude());

			if (mag1 > mag2) {
				if (getSortTypePref().equals("AESC"))
					return +1;
				else
					return -1;

			} else if (mag1 < mag2) {
				if (getSortTypePref().equals("AESC"))
					return -1;
				else
					return +1;
			} else {
				return 0;
			}
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.CANADA);
			sdf.setTimeZone(TimeZone.getDefault());
			Date dt1 = null;
			Date dt2 = null;

			try {
				dt1 = sdf.parse(movie1.getEqorigintime());
				dt2 = sdf.parse(movie2.getEqorigintime());
			} catch (ParseException e) { //
				// TODO Auto-generated catch block e.printStackTrace(); }
			}

			int hours = (int) ((dt1.getTime() - dt2.getTime()) / (60 * 60 * 1000));

			if (hours > 0) {
				if (getSortTypePref().equals("AESC"))
					return +1;
				else
					return -1;

			} else if (hours < 0) {
				if (getSortTypePref().equals("AESC"))
					return -1;
				else
					return +1;
			} else {
				return 0;
			}

		}
		/*
		 * else { SimpleDateFormat sdf = new SimpleDateFormat(
		 * "yyyy-MM-dd HH:mm:ss (z)", Locale.CANADA);
		 * sdf.setTimeZone(TimeZone.getDefault()); Date dt1 = null; Date dt2 =
		 * null;
		 * 
		 * try { dt1 = sdf.parse(movie1.getEqorigintime()); dt2 =
		 * sdf.parse(movie1.getEqorigintime()); } catch (ParseException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 * 
		 * if (Math.abs(dt1.getTime() - dt2.getTime()) > 0) { if
		 * (getSortTypePref().equals("AESC")) return +1; else return -1;
		 * 
		 * } else if (Math.abs(dt1.getTime() - dt2.getTime()) < 0) { if
		 * (getSortTypePref().equals("AESC")) return -1; else return +1; } else
		 * { return 0; } }
		 */

	}

}
