package com.jimcloudy.updateinspectionloc;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GetInspections extends Service {
	  private static final String TAG = "GetInspections";
	  private InspectionsUpdater updater;
	  public static String URL = "http://jimcloudy.comze.com/update.php?q=";
	  HttpResponse result;
	  static final int DELAY = 300000; 
	  private boolean runFlag = false;
	  InspectionData inspectionData;
	 
	  @Override
	  public IBinder onBind(Intent intent) {
	    return null;
	  }

	  @Override
	  public void onCreate() {
	    super.onCreate();
	    this.updater = new InspectionsUpdater();
	    this.inspectionData = new InspectionData(this);
	  }

	  @Override
	  public int onStartCommand(Intent intent, int flag, int startId) {
		  super.onStartCommand(intent, flag, startId);
		  this.runFlag = true;
	      this.updater.start();
	      return Service.START_STICKY;
	  }

	  @Override
	  public void onDestroy() {
	    super.onDestroy();
	    this.updater.interrupt();
	    this.updater = null;
	    this.runFlag = false;
	  }
	  
	  public InspectionData getInspectionData() { // <2>
		    return inspectionData;
	  }
	  
	  private class InspectionsUpdater extends Thread {
		  
	    public InspectionsUpdater() {
	      super("UpdaterService-InspectionsUpdater");
	    }

	    @Override
	    public void run() {
	    	GetInspections getInspections = GetInspections.this;
	    	String result = null;
	    	
	    	while(getInspections.runFlag){
	    		try{
	    			result = callWebService();
	    			if(result != null){
	    				try{
	    					ContentValues values = new ContentValues();
	    					JSONObject jsonObject = new JSONObject(result);
	    					JSONArray inspections = jsonObject.getJSONArray("inspections");
	    					Log.i("Get Inspections","Number of entries " + inspections.length());
	    					JSONObject inspection = new JSONObject();
	    					for (int i = 0; i < inspections.length(); i++) {
	    				        inspection = inspections.getJSONObject(i);
	    				        values.put(InspectionData.C_ID, inspection.getString("policy"));
	    				        values.put(InspectionData.C_NAME, inspection.getString("name"));
	    				        values.put(InspectionData.C_NAME2, inspection.getString("name2"));
	    				        values.put(InspectionData.C_ADDRESS, inspection.getString("address"));
	    				        values.put(InspectionData.C_ADDRESS2, inspection.getString("address2"));
	    				        values.put(InspectionData.C_CITYST, inspection.getString("cityst"));
	    				        values.put(InspectionData.C_ZIP, inspection.getString("zip"));
	    				        values.put(InspectionData.C_PHONE, inspection.getString("phone"));
	    				        if(getInspections.inspectionData != null){
	    				        	getInspections.inspectionData.insertOrIgnore(values);
	    				        }
	    					}
	    				}
	    				catch(Exception e){
	    					e.printStackTrace();
	    				}
	    			}
	    			Thread.sleep(DELAY);
	    		}
	    		catch(InterruptedException e){
	    			getInspections.runFlag = false;
	    		}
	    	}
	    }
	    
	    public String callWebService(){  
	        HttpClient httpclient = new DefaultHttpClient();
	        //HttpGet request = new HttpGet(URL + q);
	        //HttpPost request = new HttpPost("https://quote.nstarco.com/public/default.asp?Category=NS_Public_Test&Service=Inspections");
	        HttpPost request = new HttpPost("http://jimcloudy.comze.com/inspect/get_inspections.php");
	        HttpEntity httpEntity;
	        String line = null;
	     
	        //ResponseHandler<String> handler = new BasicResponseHandler();  
	        try {
	            result = httpclient.execute(request);
	            httpEntity = result.getEntity();
	            line = EntityUtils.toString(httpEntity);
	        } 
	        catch (ClientProtocolException e) {  
	            e.printStackTrace();  
	        } 
	        catch (IOException e) {  
	            e.printStackTrace();  
	        }
	        
	        httpclient.getConnectionManager().shutdown();   
	        return line;
	    }
	    
	   
	  }   
	}
