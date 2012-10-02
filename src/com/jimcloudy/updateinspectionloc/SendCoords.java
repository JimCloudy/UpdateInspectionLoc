package com.jimcloudy.updateinspectionloc;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.util.Xml;

public class SendCoords extends Service {
	  private static final String TAG = "SendCoords";
	  private CoordsUpdater updater;
	  public static String URL = "https://quote.nstarco.com/public/default.asp?Category=NS_Public_Test&Service=Update_Inspections";
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
	    	String query = writeXml();
	    	String result = null;
	    	String expression;
	    	XPath xpath = XPathFactory.newInstance().newXPath();
	    	
	    	if(query!=null)
	    	{
	    		result = callWebService(query);
	    	}
	    	
    		if(result != null)
    		{
    			Document doc = xmlFromString(result);	    				
    			expression = "/inspections/inspection";
    			try{
    				NodeList resultNodes = (NodeList)xpath.evaluate(expression, doc ,XPathConstants.NODESET);
					List<String> policies = new ArrayList<String>();
					for(int i=0; i<resultNodes.getLength(); i++){
						xpath = XPathFactory.newInstance().newXPath();
						expression = "policy";
						Node policyNode = (Node) xpath.evaluate(expression, resultNodes.item(i), XPathConstants.NODE);
						String policy = policyNode.getTextContent();
						xpath = XPathFactory.newInstance().newXPath();
						expression = "updated";
						Node updatedNode = (Node) xpath.evaluate(expression, resultNodes.item(i), XPathConstants.NODE);
						String updated = updatedNode.getTextContent();
						if(updated.equals("true")){
							policies.add(policy);
							Log.i("send coords ",policy);
						}
						else{
							Log.i("send coords",updated + " why");
						}
						
					}
					if(sendCoords.inspectionData != null){
						if(policies.size()!=0){
							sendCoords.inspectionData.deleteUpdatedInspections(policies);
						}
					}
    			}
    			catch(XPathExpressionException	e){
					e.printStackTrace();
				}
				catch(RuntimeException e){
					e.printStackTrace();
				}
    		}    		
	  }
	    
	    public String callWebService(String q){  
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost request = new HttpPost(URI.create(URL));
	        HttpEntity httpEntity;
	        String line = null;
	        HttpResponse response;
	     
	        try{
	        	List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
	        	nameValuePair.add(new BasicNameValuePair("m-inspect",q));
	        	request.setEntity(new UrlEncodedFormEntity(nameValuePair));
	            response = httpclient.execute(request);
	            httpEntity = response.getEntity();
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
	  	        	cursor.moveToFirst();
	  	        	while(!cursor.isAfterLast()){
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
