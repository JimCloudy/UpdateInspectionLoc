package com.jimcloudy.updateinspectionloc;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Xml;

public class SendCoords extends Service {
	  private static final String TAG = "SendCoords";
	  private CoordsUpdater updater;
	  public static String URL = "http://jimcloudy.comze.com/update.php?q=";
	  HttpResponse result;
	  InspectionData inspectionData;

	  @Override
	  public IBinder onBind(Intent intent) {
	    return null;
	  }

	  @Override
	  public void onCreate() {
	    super.onCreate();
	    this.updater = new CoordsUpdater();
	    this.inspectionData = new InspectionData(this);
	  }

	  @Override
	  public int onStartCommand(Intent intent, int flag, int startId) {
		  super.onStartCommand(intent, flag, startId);
	      this.updater.start();
	      return Service.START_STICKY;
	  }

	  @Override
	  public void onDestroy() {
	    super.onDestroy();
	    this.updater.interrupt();
	    this.updater = null;
	  }
	  
	  private class CoordsUpdater extends Thread {

	    public CoordsUpdater() {
	      super("UpdaterService-CoordsUpdater");
	    }

	    @Override
	    public void run() {
	    	SendCoords sendCoords = SendCoords.this;
	    	String query = sendCoords.updater.writeXml();
	    	String result = null;
    		result = callWebService(query);
    	
    		if(result != null)
    		{
    			inspectionData.deleteUpdatedInspections();
    		}
    		
    		
	  }
	    
	    public String callWebService(String q){  
	        HttpClient httpclient = new DefaultHttpClient();
	        //HttpGet request = new HttpGet(URL + q);
	        HttpPost request = new HttpPost("http://jimcloudy.comze.com/test.xml");
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
	        //Log.i(TAG, result);
	        return line;
	    }
	    
	    public Document xmlFromString(String xml){
	    	Document doc = null;
	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	try {
	    		DocumentBuilder db = dbf.newDocumentBuilder();
	    	    InputSource is = new InputSource();
	    	    is.setCharacterStream(new StringReader(xml));
	    	    doc = db.parse(is);
	    	} catch (ParserConfigurationException e) {
	    	    System.out.println("XML parse error: " + e.getMessage());
	    	    return null;
	    	} catch (SAXException e) {
	    	    System.out.println("Wrong XML file structure: " + e.getMessage());
	    	    return null;
	    	} catch (IOException e) {
	    	    System.out.println("I/O exeption: " + e.getMessage());
	    	    return null;
	    	}
	        return doc;
	    }
	    
	    private String writeXml(){
	        XmlSerializer serializer = Xml.newSerializer();
	        StringWriter writer = new StringWriter();
	        Cursor cursor;
	        cursor = inspectionData.getUpdatedInspections();
	        String xmlDoc = null;
	        if(cursor.getCount()!=0){
	  	        try {
	  	        	serializer.setOutput(writer);
	  	        	serializer.startDocument("UTF-8", true);
	  	        	serializer.startTag("", "inspections");
	  	        	serializer.attribute("", "number", cursor.getString(cursor.getCount()));
	  	        	cursor.moveToFirst();
	  	        	while(cursor.isAfterLast()){
	  	        		serializer.startTag("", "inspection");
	  	        		serializer.startTag("", "policy");
	  	        		serializer.text(cursor.getString(cursor.getColumnIndex("_id")));
	  	        		serializer.endTag("", "policy");
	  	        		serializer.startTag("", "lat");
	  	        		serializer.text(cursor.getString(cursor.getColumnIndex("lat")));
	  	        		serializer.endTag("", "lat");
	  	        		serializer.startTag("", "long");
	  	        		serializer.text(cursor.getString(cursor.getColumnIndex("long")));
	  	        		serializer.endTag("", "long");
	  	        		serializer.endTag("", "inspection");
	  	        		cursor.moveToNext();
	  	        	}
	  	        	serializer.endTag("", "inspections");
	  	        	serializer.endDocument();
	  	        	xmlDoc = writer.toString();
	  	        } catch (Exception e) {
	  	        	e.printStackTrace();
	  	        }
	        }
	        return xmlDoc;
	    }
	  }   
	}
