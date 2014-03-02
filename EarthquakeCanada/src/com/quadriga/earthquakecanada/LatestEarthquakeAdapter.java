package com.quadriga.earthquakecanada;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * A custom list adapter which holds a reference to all installed apps and
 * displays their respective title text in each row of a vertical list.
 * 
 * Copyright 2k11 Impressive Artworx
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @author Manuel Schwarz (m.schwarz[at]impressive-artworx.de)
 */

@SuppressLint("SimpleDateFormat")
public class LatestEarthquakeAdapter extends BaseAdapter {

	private LayoutInflater mInflaterPhone;

	private List<LatestEarthquake> eqDetails;
	/** a map which maps the package name of an app to its icon drawable */
	private Map<String, Drawable> mIconsPhone;
	private Drawable mStdImgPhone;
	private ArrayList<LatestEarthquake> arraylist;
	private static Context ctx;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            the application context which is needed for the layout
	 *            inflater
	 */
	@SuppressWarnings("static-access")
	public LatestEarthquakeAdapter(Context context,
			List<LatestEarthquake> eqDetails) {
		this.eqDetails = eqDetails;
		// cache the LayoutInflater to avoid asking for a new one each time
		mInflaterPhone = LayoutInflater.from(context);
		this.ctx = context;
		// set the default icon until the actual icon is loaded for an app
		mStdImgPhone = context.getResources().getDrawable(
				R.drawable.ic_launcher);
		this.arraylist = new ArrayList<LatestEarthquake>();
		this.arraylist.addAll(eqDetails);
	}

	@Override
	public int getCount() {
		return eqDetails.size();
	}

	@Override
	public Object getItem(int position) {
		return eqDetails.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		AppViewHolder holder;
		final LatestEarthquake eq = eqDetails.get(position);
		if (convertView == null) {
			convertView = mInflaterPhone.inflate(R.layout.earthquake_row, null);

			// creates a ViewHolder and stores a reference to the children view
			// we want to bind data to
			holder = new AppViewHolder();
			holder.eqtitle = (TextView) convertView.findViewById(R.id.eqtitle);
			holder.tvMagnitude = (TextView) convertView
					.findViewById(R.id.tvMagnitude);
			holder.tvProvience = (TextView) convertView
					.findViewById(R.id.tvProvience);
			holder.tvDatetime = (TextView) convertView
					.findViewById(R.id.tvDatetime);
			convertView.setTag(holder);

		} else {
			// reuse/overwrite the view passed assuming(!) that it is castable!
			holder = (AppViewHolder) convertView.getTag();
		}

		holder.setTitle(eq.getEqtitle());
		holder.setMagnitude(eq.getEqmagnitude());
		holder.setProvience(eq.getEqprovience());

		double mag = Double.valueOf(eq.getEqmagnitude());
		if (mag >= 1 && mag < 2.5) {
			holder.tvMagnitude.setBackgroundResource(R.drawable.lowest_eq);
		} else if (mag >= 2.5 && mag < 5) {
			holder.tvMagnitude.setBackgroundResource(R.drawable.medium_eq);
		} else {
			holder.tvMagnitude.setBackgroundResource(R.drawable.high_eq);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss (z)",
				Locale.CANADA);
		sdf.setTimeZone(TimeZone.getDefault());
		Date dt = null;
		try {
			dt = sdf.parse(eq.getEqorigintime());
		} catch (ParseException ex1) {
			// TODO Auto-generated catch block
			ex1.printStackTrace();
		}
		holder.setDatetime(sdf.format(dt));
		return convertView;
	}

	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		eqDetails.clear();
		if (charText.length() == 0) {
			eqDetails.addAll(arraylist);
		} else {
			for (LatestEarthquake wp : arraylist) {
				if (wp.getEqtitle().toLowerCase(Locale.getDefault())
						.contains(charText)) {
					eqDetails.add(wp);
				}
			}
		}
		notifyDataSetChanged();
	}

	/**
	 * Sets the list of apps to be displayed.
	 * 
	 * @param list
	 *            the list of apps to be displayed
	 */
	public void setListItems(List<LatestEarthquake> list) {
		eqDetails = list;
	}

	/**
	 * Sets the map containing the icons for each displayed app.
	 * 
	 * @param icons
	 *            the map which maps the app's package name to its icon
	 */
	public void setIcons(Map<String, Drawable> icons) {
		this.mIconsPhone = icons;
	}

	/**
	 * Returns the map containing the icons for each displayed app.
	 * 
	 * @return a map which contains a mapping of package names to icon drawable
	 *         for all displayed apps
	 */
	public Map<String, Drawable> getIcons() {
		return mIconsPhone;
	}

	/**
	 * A view holder which is used to re/use views inside a list.
	 */
	public class AppViewHolder {

		private TextView eqtitle;
		private TextView tvMagnitude;
		private TextView tvProvience;
		private TextView tvDatetime;

		/**
		 * Sets the text to be shown as the app's title
		 * 
		 * @param title
		 *            the text to be shown inside the list row
		 */
		public void setTitle(String title) {
			eqtitle.setText(title);
		}

		public void setMagnitude(String magnitude) {
			tvMagnitude.setText(magnitude);
		}

		public void setProvience(String provience) {
			tvProvience.setText(provience);
		}

		public void setDatetime(String datetime) {
			tvDatetime.setText(datetime);
		}

	}
}
