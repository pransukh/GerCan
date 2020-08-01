package com.fis.paymentGateway;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;





import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.java.twilio.SENDSMS;

public class RestClient {

	private int RETURN_TRUE=2175;
	private int RETURN_FALSE=2076;
	private int RETURN_ERROR=2077;
	public final static String URL = "http://localhost:8080/RestWithJerrsy/member/simpleMessageService";
	public JSONObject gObjJSON=null;
	public int postRequestData(String resourceURL,String resourceData)
	{
		System.out.println("******************** in postRequestData() ********************");
		System.out.println("******************** EndPoint URL:- "+resourceURL);
		HttpURLConnection httpConnection=null;
		int responseCode=0;
		String responseMessage="";	
		InputStream objIncomingData=null;
		String readInputStream;
		BufferedReader gObjBufferedReader=null;
		StringBuilder gIncomingJSONData=new StringBuilder();
		
		String RESPONSE_MESSAGE="";
		
		try
		{
			URL targetUrl = new URL(resourceURL);
			httpConnection = (HttpURLConnection) targetUrl.openConnection();
			httpConnection.setDoOutput(true);
			httpConnection.setRequestMethod("POST");
			httpConnection.setDoInput(true);  
			httpConnection.setUseCaches(false);	     
			httpConnection.setRequestProperty("Content-Type", "application/json");
			httpConnection.setRequestProperty("Accept", "text/plain");
			DataOutputStream out = new DataOutputStream(httpConnection.getOutputStream());
			out.writeBytes(resourceData);
	        out.flush();  
	        out.close();  
	        System.out.println("******************** response sent ********************");
	        responseCode=httpConnection.getResponseCode();
	        responseMessage=httpConnection.getResponseMessage();
	        System.out.println("******************** response Code:-"+responseCode);
	        System.out.println("******************** response Message:-"+responseMessage);
			//logDebugInfo("Response code: "+responseCode+" , Response Message: "+responseMessage, LOG_TYPE_CRITICAL);
	        
	        
	        if(httpConnection.getResponseCode() == 200 || httpConnection.getResponseCode() == 201 || httpConnection.getResponseCode() == 202  )
	        {
		        objIncomingData = httpConnection.getInputStream();	
		        gObjBufferedReader = new BufferedReader(new InputStreamReader(objIncomingData));
				while ((readInputStream = gObjBufferedReader.readLine()) != null) 
				{
					gIncomingJSONData.append(readInputStream);
				}
		        
				gObjJSON = (JSONObject) new JSONValue().parse(""+gIncomingJSONData.toString());
				//web service response is not confirmed yet
				//RESPONSE_MESSAGE=gObjJSON.get("MESSAGE").toString();//It will received in JSON response .
				System.out.println(RESPONSE_MESSAGE);
		        return RETURN_TRUE;
	        }  
	        if(responseCode == 400 ||responseCode == 401 ||responseCode == 403 ||responseCode == 404){
	        	setExceptionMessage("Exception occurred while Sending Study information : "+responseMessage, "Message is not processed as getting response code:"+responseCode);
	        	return RETURN_ERROR;
	        }	        
	        if(responseCode == 500 ||responseCode == 501 ||responseCode == 502 ||responseCode == 503 ||responseCode == 504 ||responseCode == 505){
	        	setExceptionMessage("Exception occurred while Sending Study information : "+responseMessage, "Message is not processed as getting response code:"+responseCode);
	        	return RETURN_ERROR;
	        }
	        
	    	return RETURN_TRUE;	        
		}
		catch(java.net.ConnectException timeOutException)
		{
			//logDebugInfo("Exception: "+timeOutException, LOG_TYPE_CRITICAL);
			setExceptionMessage("Exception occurred while submitting resource information: "+timeOutException.getMessage(), "Message not processed as exception occurred inside postRequestDatafunction: "+timeOutException);
			return RETURN_ERROR;
		}
		
		catch(Exception exceptionMessage)
		{
			//logDebugInfo("Exception inside postRequestData function :"+exceptionMessage.getMessage(),LOG_TYPE_CRITICAL);
			setExceptionMessage("Exception occurred while submitting resource  information :"+exceptionMessage, "Exception inside postRequestDatafunction :"+exceptionMessage);
			exceptionMessage.printStackTrace();
		}
		finally{
			   try{
			    if (httpConnection != null)
			    {
			    	httpConnection.disconnect();
			    }
			   }catch(Exception objException){
			    //logDebugInfo("Exception inside final block of postRequestData() function :"+objException.getMessage(),LOG_TYPE_CRITICAL);
			    setExceptionMessage("Exception occurred while submitting resource information :"+objException.getMessage(), "Exception inside postRequestDatafunction :"+objException);
			   }
			 }
		return RETURN_ERROR;
	}
	
	void setExceptionMessage(String asdf,String asd){
		System.out.println(asdf);
		System.out.println(asd);
	}
	
	/*public static void main(String[] args) throws Exception {
		JSONObject obj=new JSONObject();
		obj.put("name", "Sandeep");
		obj.put("bankAcc", "ICICI124578");
		
		 
		
		
		//new RestClient().postRequestData(URL, obj.toString());
		//new SENDSMS().main(null);
	}*/
}

/*{
	"name":"Sandeep",
	"bankAcc":"ICICI124578"
}*/