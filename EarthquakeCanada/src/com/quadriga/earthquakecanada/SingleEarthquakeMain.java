package com.quadriga.earthquakecanada;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SingleEarthquakeMain extends SherlockFragmentActivity {

	// private LinearLayout llProgress;
	private TextView tvLocalDatetime;
	private TextView tvPoints;
	private TextView tvTitle;
	private TextView tvDepth;
	private TextView tvMagnitudeAndType;
	private Button btnViewMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_eathquake_details);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		findViewById();

		GetStart StartTaskObject = new GetStart();
		StartTaskObject.execute();

	}

	public void findViewById() {
		// llProgress = (LinearLayout) findViewById(R.id.llProgress);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvLocalDatetime = (TextView) findViewById(R.id.tvLocalDatetime);
		tvPoints = (TextView) findViewById(R.id.tvPoints);
		tvDepth = (TextView) findViewById(R.id.tvDepth);
		tvMagnitudeAndType = (TextView) findViewById(R.id.tvMagnitudeAndType);
		btnViewMap = (Button) findViewById(R.id.btnViewMap);

		btnViewMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SingleEarthquakeMain.this,
						EqGmap.class);
				intent.putExtra("full_title", tvTitle.getText());
				intent.putExtra("datetime", tvLocalDatetime.getText());
				intent.putExtra("geolon", SingleEarthquakeMain.this.getIntent()
						.getStringExtra("geolon").toString());
				intent.putExtra("geolat", SingleEarthquakeMain.this.getIntent()
						.getStringExtra("geolat").toString());
				intent.putExtra("depth", tvDepth.getText());
				intent.putExtra("magnitude", tvMagnitudeAndType.getText());
				startActivity(intent);
			}
		});

		// latest_eqlist = (ListView) findViewById(R.id.latest_eqlist);
		// latest_eqlist.setOnItemClickListener(this);
		// latest_eqlist.setOnItemLongClickListener(this);
	}

	class GetStart extends AsyncTask<Object, Void, String> {
		@Override
		public void onPreExecute() {
			super.onPreExecute();
			// llProgress.setVisibility(View.VISIBLE);
		}

		@Override
		public String doInBackground(Object... params) {
			doWork();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			tvTitle.setText(SingleEarthquakeMain.this.getIntent()
					.getStringExtra("full_title").toString().trim());
			tvLocalDatetime.setText(SingleEarthquakeMain.this.getIntent()
					.getStringExtra("datetime").toString().trim());
			tvPoints.setText(SingleEarthquakeMain.this.getIntent()
					.getStringExtra("geolat").toString()
					+ " (lat) and "
					+ SingleEarthquakeMain.this.getIntent()
							.getStringExtra("geolon").toString() + " (lon)");
			tvDepth.setText(SingleEarthquakeMain.this.getIntent()
					.getStringExtra("depth").toString().trim()
					+ " KM");
			tvMagnitudeAndType.setText(SingleEarthquakeMain.this.getIntent()
					.getStringExtra("magnitude").toString().trim());
		}
	}

	public void doWork() {

	}
}
