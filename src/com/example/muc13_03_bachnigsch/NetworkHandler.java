package com.example.muc13_03_bachnigsch;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;

/**
 * 
 * @author Max Nigsch
 * @author Martin Bach
 * 
 * Hilfsklasse die kompletten Netzwerkverkehr regelt
 *
 */

public class NetworkHandler  {

	private static final String DEBUG_TAG = "HttpExample";
	private int receivedAzimut;
	private boolean sendMode;
	private SearchActivity searchActivity;
	
	// Konstruktoren
	public NetworkHandler(){
	}
	
	public NetworkHandler(SearchActivity searchActivity){
		this.searchActivity = searchActivity;
	}
	
	// Sendet uebergebenen String - ruft async Task auf
	public void sendData(String sendUri) {	
		sendMode = true;
		new DownloadWebpageTask().execute(sendUri,"PUT");	
	}
	
	// Empfaengt Daten - ruft async Task auf
	public void receiveData(String receiveUri){
		sendMode = false;
		new DownloadWebpageTask().execute(receiveUri,"GET");
	}
	
	// Uses AsyncTask to create a task away from the main UI thread. This task takes a 
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
       @Override
       protected String doInBackground(String... urls) {
             
           // params comes from the execute() call: params[0] is the url.
           try {
        	   //  fetches and processes the web page content. When it finishes, it passes back a result string.
               return downloadUrl(urls[0],urls[1]);
           } catch (IOException e) {
               return "Unable to retrieve web page. URL may be invalid.";
           }
       }
       // onPostExecute displays the results of the AsyncTask.
       @Override
       protected void onPostExecute(String result) {
    	   System.out.println("Empfangener String: " + result);
    	   // muss im Falle des Sendens nicht ausgefuehrt werden
    	   if (sendMode == false){
	    	   try {  
		    		JSONObject object = (JSONObject) new JSONTokener(result).nextValue();	
		    		JSONArray list = object.getJSONArray("list");
		    		
		    		// Alle Elemente aus Empfangenem String auswerten und nach Benutzer "mussi0815" suchen
		    		for(int i = 0 ; i < list.length() ; i++ ){
		    				JSONObject listObject = list.optJSONObject(i);
		    			if(listObject.getString("user").equals("mucci0815")){
		    				receivedAzimut = listObject.getInt("orientation");
		    			}
		    		}
	    	   } 
	    	   catch (JSONException e) {
					e.printStackTrace();
	    	   }    	   
		       
		       System.out.println("Received Azimut: " + receivedAzimut);    
		      searchActivity.setAzimut(receivedAzimut);
		    }
       	}	  
   }
    

	 // Given a URL, establishes an HttpUrlConnection and retrieves
	 // the web page content as a InputStream, which it returns as
	 // a string.
	 private String downloadUrl(String myurl,String requestMethod) throws IOException {
		 
	     InputStream is = null;	     
	     int len = 1000;
	         
	     try {
	         URL url = new URL(myurl);
	         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	         conn.setReadTimeout(10000 /* milliseconds */);
	         conn.setConnectTimeout(15000 /* milliseconds */);
	         conn.setRequestMethod(requestMethod);
	         conn.setDoInput(true);
	         // Starts the query
	         conn.connect();
	         int response = conn.getResponseCode();
	         Log.d(DEBUG_TAG, "The response is: " + response);
	         is = conn.getInputStream();
	
	         // Convert the InputStream into a string
	         String contentAsString = readIt(is, len);
	         return contentAsString;
	         
	     // Makes sure that the InputStream is closed after the app is
	     // finished using it.
	     } finally {
	         if (is != null) {
	             is.close();
	         } 
	     }
	 }

	 
	// Reads an InputStream and converts it to a String.
	 public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
	     Reader reader = null;
	     reader = new InputStreamReader(stream, "UTF-8");        
	     char[] buffer = new char[len];
	     
	     if(sendMode == false){ 
	    	 int l = reader.read(buffer);
	    	 return new String(buffer, 0, l);
	     }
	     else{
	    	 reader.read(buffer);
	    	 return new String(buffer); 
	     }
	 }
}
