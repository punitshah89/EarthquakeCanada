package com.quadriga.earthquakecanada;

import java.util.List;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

public class EqGmap extends MapActivity {
	private MapView mapView;
	private float logni,latti;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.googlemap);
		mapView = (MapView) findViewById(R.id.map_view);       
        mapView.setBuiltInZoomControls(true);
        
        logni=Float.valueOf(this.getIntent().getStringExtra("geolon"));
        //logni=Float.valueOf("-70.028");
        latti=Float.valueOf(this.getIntent().getStringExtra("geolat"));
        //latti=Float.valueOf("47.49");
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.mapicon);
		CustomItemizedOverlay itemizedOverlay = new CustomItemizedOverlay(drawable, this);
		
		GeoPoint point = new GeoPoint((int)(latti * 1E6), (int)(logni * 1E6));
		OverlayItem overlayitem = new OverlayItem(point,"Earthquake Detail",this.getIntent().getStringExtra("magnitude").toString().trim()+" - "+this.getIntent().getStringExtra("full_title").toString().trim() + "\n" + " Depth" + this.getIntent().getStringExtra("depth").toString().trim() + "\n Date:" +  this.getIntent().getStringExtra("datetime").toString().trim());
		//OverlayItem overlayitem = new OverlayItem(point,"Earthquake Detail","magnitude - title");
		Toast.makeText(this,"Tap on icon to view Earthquake detail",Toast.LENGTH_SHORT).show();
		itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedOverlay);
		
		MapController mapController = mapView.getController();
		
		mapController.animateTo(point);
		mapController.setZoom(4);
	}
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
