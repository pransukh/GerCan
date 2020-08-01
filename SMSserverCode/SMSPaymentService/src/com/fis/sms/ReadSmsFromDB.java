package com.fis.sms;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;







import com.fis.paymentGateway.RestClient;
import com.fis.resources.beans.TextDBData;
import com.java.twilio.SENDSMS;

import java.sql.PreparedStatement;

import org.json.JSONException;
import org.json.JSONObject;

public class ReadSmsFromDB implements Runnable 
{

	String drivers,server,port,database,url,username,password,dateFormat=null;
	long sleepTimeOut=0;
	String devState=null;
	private final static String  SERVER="[SERVER]";
	private final static String  PORT="[PORT]";
	private final static String  DATABASE="[DATABASE]";
	private static Connection dbconn = null;
	private final static String dbResourceName = "DBConfig.properties"; // could also be a constant
	private final static String connectionResourceName = "connection.properties";
	private  ArrayList<TextDBData> textDataList = null;
	
public Properties getProperties(String resourceName) throws IOException
{
	System.out.println("******************** in getProperties() for: "+resourceName+" *******************");
	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	Properties props = new Properties();
	try(InputStream resourceStream = loader.getResourceAsStream(resourceName)) 
	{
	    props.load(resourceStream);
	    System.out.println("props"+ props);
	}
	catch(Exception e){
		System.out.println("Exception in getProperties() for: "+resourceName+". Exception:-"+e);
		e.printStackTrace();
	}
	System.out.println("******************** END of getProperties() for: "+resourceName+" *******************");
	return props;
	
	
	
	
}
	
boolean setParameters(Properties props)
{
	System.out.println("******************** in setParameters() *******************");
	drivers=props.get("DRIVERS").toString();
	server=props.get("SERVER").toString();
	port=props.get("PORT").toString();
	database=props.get("DATABASE").toString();
	url=props.get("URL").toString();
	username=props.get("USERNAME").toString();
	password=props.get("PASSWORD").toString();
	sleepTimeOut=Long.parseLong(props.get("SLEEP_TIMEOUT").toString());
	dateFormat=props.get("DATE_FORMAT").toString();
	devState = props.get("DEV_STATE").toString();
	
	url=url.replace(SERVER, server);
	url=url.replace(PORT, port);
	url=url.replace(DATABASE, database);
	System.out.println(url+" and Dev State:"+devState);
	
	DBConnection dbinstance = DBConnection.getInstance();
	dbconn = dbinstance.getDBConnection(drivers, url, username, password);
	
	if(dbconn!=null){
		System.out.println("******************** DBObject accuired. *******************");
		System.out.println("******************** END of setParameters() *******************");
		return true;
	}
	else
		return false;
}

void killDBConnection(){
	dbconn=null;
	System.err.println("DBConnection killed.");
}

Connection callDBConnection()
{
	DBConnection dbinstance = DBConnection.getInstance();
	dbconn = dbinstance.getDBConnection(drivers, url, username, password);
	System.err.println("DBConnection gained.");
	return dbconn;
}
	
	public void run()
	{
		System.out.println("******************** in run() *******************");
		
		while (true)
		{
			
			
			try 
			{
				ArrayList<TextDBData> dataToProcess = fetchText(dbconn);
				
				if(dataToProcess.size()>0){
					System.out.println("Calling processDBRequest.....");
					processDBRequests(dataToProcess);
				}
				
				System.out.println("Child Thread is sleeping for:= "+sleepTimeOut);
				Thread.currentThread().sleep(sleepTimeOut);
				
			}
			catch (SQLException sqlExcep){
				System.err.println("Exception whilel fetching records.");
				break;
			}
			catch (Exception e) {
				System.err.println("Child Thread Exception in sleep."+e);
				e.printStackTrace();
				break;
			}
		}
		
	}
	
	public ArrayList<TextDBData> fetchText(Connection dbconn) throws SQLException
	{
		System.out.println("******************** in fetchText() *******************");
		killDBConnection();
		callDBConnection();
		textDataList = null;
		textDataList = new ArrayList<TextDBData>();
		try {
			
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat); 
			Statement stmt=dbconn.createStatement(); 
			PreparedStatement pr = dbconn.prepareStatement("select * from ozekimessagein where status=? and msg like'%receiverMobile%' limit ?");
			pr.setString(1, "NEW_SMS");
			pr.setInt(2, 1);
			ResultSet rs=pr.executeQuery(); 
			
			while(rs.next())
			{
				TextDBData textDBData = new TextDBData();
				
				/*textDBData.setId(Integer.parseInt(rs.getString("Id")));
				Date recDate = formatter.parse(rs.getString("SendTime"));
				textDBData.setReceivedTime(recDate);
				
				textDBData.setMessageFrom(rs.getString("MessageFrom"));
				textDBData.setMessageText(rs.getString("MessageText"));*/
				
				textDBData.setId(Integer.parseInt(rs.getString("Id")));
				Date sentDate = formatter.parse(rs.getString("senttime"));
				textDBData.setReceivedTime(sentDate);
				
				Date recDate = formatter.parse(rs.getString("receivedtime"));
				textDBData.setReceivedTime(recDate);
				
				textDBData.setMessageFrom(rs.getString("sender"));
				textDBData.setMessageText(rs.getString("msg"));
				
				textDataList.add(textDBData);
				
			}
			System.out.println("******************** Number of New SMS:-"+textDataList.size() +" *******************");
		} 
		
		catch (NumberFormatException e) {
			System.err.println(e);
			
		} 
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("******************** sending back the payload from fetchText() *******************");
		return textDataList;
	}
	public void processDBRequests(ArrayList<TextDBData> rawData)
	{
		System.out.println("******************** in processDBRequests() *******************");
		RestClient gateWay = new RestClient();
		try {
			System.out.println("ID	|	DATE	|	TEXT	|	FROM");
			for(TextDBData data:rawData){
				System.out.println("");
				System.out.print(data.getId()+"|"+data.getReceivedTime()+"|"+data.getMessageText()+"|"+data.getMessageFrom());
				
				System.err.println("\n******************** in Calling PaymentGateWay *******************");
				
				//JSONObject jsonObject = new JSONObject(data.getMessageText().toString());
				Properties props = this.getProperties(connectionResourceName);
				String URL_TO_HIT="";
				if(data.getMessageText().toString().contains("amount"))
				{
					URL_TO_HIT = props.get("PAYMENT_HUB_OTP_URL").toString(); // To generate otp
				}
				else if(data.getMessageText().toString().contains("otp"))
				{
					URL_TO_HIT = props.get("PAYMENT_HUB_URL").toString(); // To make payments
				}
				if(devState.equalsIgnoreCase("YES"))
				{
					if(gateWay.postRequestData(RestClient.URL, data.getMessageText()) == 2175){
						System.out.println(gateWay.gObjJSON);
					}
				}else{
					
					if(props.get("LOAD").toString().equalsIgnoreCase("DONE"))
					{
						if(gateWay.postRequestData(URL_TO_HIT, data.getMessageText()) == 2175){
							
							System.out.println("******************** <PaymentGateWay Response> *******************");
							System.out.println(gateWay.gObjJSON);
							System.out.println("******************** </PaymentGateWay Response> *******************");
						}
					}
				}
				/*
				 * 
				 * 
				 * SEND SMS WITH OZENKING SERVER
				 * 
				 * 
				 * 
				 * */
				/*System.out.println("******************** Pushing DB to send sms *******************");
				if(pushSMSInDB(dbconn,gateWay.gObjJSON.get("senderPhoneNo").toString(), gateWay.gObjJSON.get("message").toString())){
					System.out.println("******************** SMS PUSHED *******************");
				}*/
				
				/*
				 * 
				 * Updating processed sms
				 * 
				 * */
				/*if(updateProcessedSMSStatus(dbconn,data.getId())){
					System.out.println("******************** END PROCESS *******************");
				}*/
				
				/*
				 * 
				 * 
				 * SEND SMS WITH TWILIO ACCOUNT
				 * 
				 * 
				 * */
				/*SENDSMS sendsms = new SENDSMS();
				String smsID = sendsms.pushSMSWithTwilio(gateWay.gObjJSON.get("senderContract").toString(), gateWay.gObjJSON.get("message").toString());
				if(smsID.length() > 0){
					Statement stmt=dbconn.createStatement(); 
					PreparedStatement pr = dbconn.prepareStatement("update ozekimessagein set status=? where id=?");
					pr.setString(1, "Sent");
					pr.setInt(2, data.getId());
					if(pr.executeUpdate() > 0){
						System.out.println("SMS Updated Successfully.");
					}
				}*/
				/*jsonObject.put("receiverContract", data.getReceiverContract());
				jsonObject.put("senderContract", data.getMessageFrom());
				jsonObject.put("otp", data.getMessageText());*/
				
			
			}
		} catch (Exception e) {
			System.out.println("in ProcessDBReq exception.");
			e.printStackTrace();
		}
	}

	private boolean pushSMSInDB(Connection dbconn,String smsTo,String smsText) throws Exception
	{
		
		System.out.println("******************** in pushSMSInDB() *******************");
		boolean status = true;
		if(!smsTo.contains("+91")){
			System.out.println("+91"+smsTo);
		}
		 
		PreparedStatement pr = dbconn.prepareStatement("insert into ozekimessageout (receiver,msg,status) values (?,?,?) ");
		pr.setString(1, smsTo);
		pr.setString(2, smsText);
		pr.setString(3, "send"); // 'send' is keyword used to send sms from DB.
		status = pr.execute();
		/*if(status)
		{
			status = true;
		}else{
			status = false;
		}*/
		System.out.println("******************** sending status as :-"+status+" from pushSMSInDB *******************");
		return status;
	}
	private boolean updateProcessedSMSStatus(Connection dbconn,int smsID) throws Exception
	{
		System.out.println("******************** Updating processed sms status, ID: "+smsID+"  *******************");
		Statement stmt=dbconn.createStatement(); 
		PreparedStatement pr = dbconn.prepareStatement("update ozekimessagein set status=? where id=?");
		pr.setString(1, "Sent");
		pr.setInt(2, smsID);
		if(pr.executeUpdate() > 0){
			System.out.println("******************** SMS Processed and Updated Successfully., ID: "+smsID+"  *******************");
			return true;
		}else
			System.err.println("******************** SMS NOT Updated, ID: "+smsID+"  *******************");
			return false;
	}
	void init()
	{
		try {
			Properties props = this.getProperties(dbResourceName);
			if(props.get("LOAD").toString().equalsIgnoreCase("DONE")){
				if(setParameters(props))
				{
					Thread thread = new Thread(this);
					System.out.println("******************** Starting Thread *******************");
					thread.start();
				}
			}else{
				System.err.println("******************** DB properties not loaded. *******************");
				System.err.println("******************** SYSTEM BREAKS HERE !! *******************");
			}
			
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	public static void main(String[] args) 
	{
		new ReadSmsFromDB().init();
	}
}
