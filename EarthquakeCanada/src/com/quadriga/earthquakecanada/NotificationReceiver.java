package com.quadriga.earthquakecanada;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class NotificationReceiver extends BroadcastReceiver {

	public static String ACTION_ALARM = "com.alarammanager.alaram";

	private String strTime;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			strTime = sharedPreferences.getString("notification_status", "30");
			Bundle bundle = intent.getExtras();
			String action = bundle.getString(ACTION_ALARM);

			if (action.equals(ACTION_ALARM)) {
				Intent inService = new Intent(context, EqTaskService.class);
				context.startService(inService);
			}

		} catch (Exception e) {
			// TODO: handle exception
			try {
				if (!strTime.equals("NA")) {
					AlarmManager alarmManager = (AlarmManager) context
							.getSystemService(Context.ALARM_SERVICE);

					Intent inService = new Intent(context,
							NotificationReceiver.class);
					inService.putExtra(NotificationReceiver.ACTION_ALARM,
							NotificationReceiver.ACTION_ALARM);
					PendingIntent pIntent = PendingIntent.getBroadcast(context,
							1234567, inService,
							PendingIntent.FLAG_UPDATE_CURRENT);
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
							System.currentTimeMillis(),
							(Integer.valueOf(strTime) * 60 * 1000), pIntent);
				}

			} catch (Exception e1) {
				// TODO: handle exception
				// Toast.makeText(context,"e1" + " " + e1.toString(),
				// Toast.LENGTH_SHORT).show();
			}
		}
	}
}
