package com.jimcloudy.updateinspectionloc;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;

public class GetInspections extends Service {
	  private static final String TAG = "GetInspections";
	  private InspectionsUpdater updater;
	  public static String URL = "";
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
	    	String query = "hello";
	    	String result = null;
	    	XPath xpath = XPathFactory.newInstance().newXPath();
	    	String expression;
	    	TelephonyManager phone = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	    	
	    	
	    	while(getInspections.runFlag){
	    		try{
	    			result = callWebService(phone.getLine1Number());
	    			if(result != null)
	    			{
	    				Document doc = xmlFromString(result);	    				
	    				expression = "/inspections/inspection";

	    				try{
	    					NodeList resultNodes = (NodeList)xpath.evaluate(expression, doc ,XPathConstants.NODESET);
	    					ContentValues values = new ContentValues();
	    					for(int i=0; i<resultNodes.getLength(); i++){
	    						xpath = XPathFactory.newInstance().newXPath();
	    						expression = "policy";
	    						Node node = (Node) xpath.evaluate(expression, resultNodes.item(i), XPathConstants.NODE);
	    						String content = node.getTextContent();
	    						values.put(InspectionData.C_ID, content);
	    						xpath = XPathFactory.newInstance().newXPath();
	    						expression = "name1";
	    						node = (Node) xpath.evaluate(expression, resultNodes.item(i), XPathConstants.NODE);
	    						content = node.getTextContent();
	    						values.put(InspectionData.C_NAME, content);
	    						xpath = XPathFactory.newInstance().newXPath();
	    						expression = "name2";
	    						node = (Node) xpath.evaluate(expression, resultNodes.item(i), XPathConstants.NODE);
	    						content = node.getTextContent();
	    						values.put(InspectionData.C_NAME2, content);
	    						xpath = XPathFactory.newInstance().newXPath();
	    						expression = "address1";
	    						node = (Node) xpath.evaluate(expression, resultNodes.item(i), XPathConstants.NODE);
	    						content = node.getTextContent();
	    						values.put(InspectionData.C_ADDRESS, content);
	    						xpath = XPathFactory.newInstance().newXPath();
	    						expression = "address2";
	    						node = (Node) xpath.evaluate(expression, resultNodes.item(i), XPathConstants.NODE);
	    						content = node.getTextContent();
	    						values.put(InspectionData.C_ADDRESS2, content);
	    						xpath = XPathFactory.newInstance().newXPath();
	    						expression = "cityst";
	    						node = (Node) xpath.evaluate(expression, resultNodes.item(i), XPathConstants.NODE);
	    						content = node.getTextContent();
	    						values.put(InspectionData.C_CITYST, content);
	    						xpath = XPathFactory.newInstance().newXPath();
	    						expression = "zip";
	    						node = (Node) xpath.evaluate(expression, resultNodes.item(i), XPathConstants.NODE);
	    						content = node.getTextContent();
	    						values.put(InspectionData.C_ZIP, content);
	    						xpath = XPathFactory.newInstance().newXPath();
	    						expression = "phone";
	    						node = (Node) xpath.evaluate(expression, resultNodes.item(i), XPathConstants.NODE);
	    						content = node.getTextContent();
	    						values.put(InspectionData.C_PHONE, content);
	    						if(getInspections.inspectionData != null){
	    							getInspections.inspectionData.insertOrIgnore(values);
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
	    			Thread.sleep(DELAY);
	    		}
	    		catch(InterruptedException e){
	    			getInspections.runFlag = false;
	    		}
	    	}
	    }
	    
	    public String callWebService(String q){  
	        HttpClient httpclient = new DefaultHttpClient();
	        //HttpGet request = new HttpGet(URL + q);
	        HttpPost request = new HttpPost(URL);
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
	  }   
	}
