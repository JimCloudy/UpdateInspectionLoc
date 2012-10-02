package com.jimcloudy.updateinspectionloc;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class LocationMap extends MapActivity{ 
	Bundle extras;
	MapView mapView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.location_map);
    	mapView = (MapView) findViewById(R.id.mapview);
    	mapView.setBuiltInZoomControls(true);
    	List<Overlay> mapOverlays = mapView.getOverlays();
    	Drawable drawable = this.getResources().getDrawable(R.drawable.pin);
    	LocItemizedOverlay itemizedoverlay = new LocItemizedOverlay(drawable, this);
    	this.extras = getIntent().getExtras();
    	if(extras!=null){
    		int LAT = (int)(this.extras.getDouble("lat") * 1e6);
    		int LONG = (int)(this.extras.getDouble("long") * 1e6);
    		GeoPoint point = new GeoPoint(LAT,LONG);
        	OverlayItem overlayitem = new OverlayItem(point, "Inspection Map", "Current Location");
        	itemizedoverlay.addOverlay(overlayitem);
        	mapOverlays.add(itemizedoverlay);
        	MapController mc = mapView.getController();
        	mc.setCenter(point);
        	mc.setZoom(17);    		
    	}
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
