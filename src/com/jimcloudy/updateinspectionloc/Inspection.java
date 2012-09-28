package com.jimcloudy.updateinspectionloc;

import com.jimcloudy.updateinspectionloc.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Inspection extends Activity implements LocationListener, OnClickListener{

	public final String TAG = "Inspection";
	LocationManager locationManager;
	Location lastLocation;
	Geocoder geocoder;
	TextView textLoc;
	TextView textPolicy;
	TextView textInsured;
	TextView textInsured2;
	TextView textAddress;
	TextView textAddress2;
	String provider;
	Button getLoc;
	Button sendLoc;
	InspectionData inspectionData;
	Bundle extras;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        getLoc = (Button) findViewById(R.id.buttonGetLoc);
        getLoc.setOnClickListener(this);
        sendLoc = (Button) findViewById(R.id.buttonSendLoc);
        sendLoc.setOnClickListener(this);
        		
        textLoc = (TextView) findViewById(R.id.textLoc);
        textPolicy = (TextView) findViewById(R.id.textInspectionPolicy);
        textInsured = (TextView) findViewById(R.id.textInspectionInsured);
        textInsured2 = (TextView) findViewById(R.id.textInspectionInsured2);
        textAddress = (TextView) findViewById(R.id.textInspectionAddress);
        textAddress2 = (TextView) findViewById(R.id.textInspectionAddress2);
        this.extras = getIntent().getExtras();
        if(extras!=null){
        	textPolicy.setText(this.extras.getString("policy"));
        	textInsured.setText(this.extras.getString("name"));
        	textInsured2.setText(this.extras.getString("name2"));
        	textAddress.setText(this.extras.getString("address"));
        	textAddress2.setText(this.extras.getString("address2"));
        }
        
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        geocoder = new Geocoder(this);
        
        Criteria criteria = new Criteria();
        //provider = this.locationManager.getBestProvider(criteria, false);
        provider = LocationManager.GPS_PROVIDER;
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    }
    
    public void onClick(View v){
    	if(v.getId() == R.id.buttonGetLoc){
    		this.lastLocation = this.locationManager.getLastKnownLocation(provider);
    		if(this.lastLocation != null){
    			String text = String.format("LAT:\t %f\nLONG:\t %f", this.lastLocation.getLatitude(),this.lastLocation.getLongitude());
    			textLoc.setText(text);
    		}
    		else{
    			textLoc.setText("Location not available");
    		}
    	}
    	if(v.getId() == R.id.buttonSendLoc){
    		if(this.lastLocation == null){
    			this.inspectionData = new InspectionData(this);
    			String[] policy = new String[]{this.extras.getString("policy")};
    			ContentValues values = new ContentValues();
    			values.put(InspectionData.C_LAT, this.lastLocation.getLatitude());
    			//values.put(InspectionData.C_LAT, "44.601656");
    			values.put(InspectionData.C_LONG, this.lastLocation.getLongitude());
    			//values.put(InspectionData.C_LONG, "-95.678194");
    			if(this.inspectionData.updateInspectionByPolicy(policy, values)){
    				startService(new Intent(this,SendCoords.class));
    				stopService(new Intent(this,SendCoords.class));
    				Toast.makeText(this, "Inspection Updated", Toast.LENGTH_LONG).show();
    			}
    			else{
    				Toast.makeText(this, "Please Try Again", Toast.LENGTH_LONG).show();
    			}
    		}
    		else{
    			Toast.makeText(this, "Please Update Location", Toast.LENGTH_LONG).show();
    		}
    	}
    }
    
    @Override
    public void onResume(){
    	super.onRestart();
    	this.locationManager.requestLocationUpdates(provider, 1000, 10, this);
    	//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
    }

    @Override
    protected void onPause(){
    	super.onPause();
    	this.locationManager.removeUpdates(this);
    }
    
    //@Override
    public void onLocationChanged(Location location){    	
    }
    
    public void onProviderDisabled(String provider){
    }
    public void onProviderEnabled(String provider){
    }
    public void onStatusChanged(String provider,int status,Bundle extras){
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { 
    	startActivity(new Intent(this,InspectionList.class));
    	return true;
    }
}
