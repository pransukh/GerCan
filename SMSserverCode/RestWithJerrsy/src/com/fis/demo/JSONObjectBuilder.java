package com.fis.demo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import com.fis.pojo.MemberPOJO;
import com.fis.pojo.SMSData;
import com.fis.pojo.SMSResponse;
import com.fis.pojo.StatusPOJO;

public class JSONObjectBuilder {

	public JSONObject getJsonData(InputStream requestData) {
		System.out.println("in getJsonData() "+requestData );
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(requestData));
		StringBuilder stringBuilder = new StringBuilder();

		JSONObject inputJsonData = null;
		String dataChunk="";
		try 
		{
			while((dataChunk = bufferedReader.readLine()) != null){
				stringBuilder.append(dataChunk);
			}
			inputJsonData = new JSONObject(stringBuilder.toString());
		} catch (Exception e) {
			System.out.println("Exception While Parsing input Data:- "+ e);
		}
		finally{
			bufferedReader = null;
			stringBuilder=null;
		}

		System.out.println("DATA IN JSON FORMAT :- "+ inputJsonData);
		return inputJsonData;
	}
	
	MemberPOJO jsonParser(JSONObject data, MemberPOJO memberPOJO) {
		System.out.println("in jsonParser() ");
		try {
			memberPOJO.setName(data.getString("name").toString());
			memberPOJO.setBankAcc(data.getString("bankAcc").toString());
		} catch (JSONException e) {
			System.err.println("ERROR IN PARSING DATA :- "+e);
		}
		return memberPOJO;
	}
	SMSData jsonParserSMS(JSONObject data, SMSData smsData) {
		System.out.println("in jsonParser() ");
		try {
			smsData.setReceiverContract(data.getString("receiverMobile").toString());
			smsData.setSenderContract(data.getString("senderPhoneNo").toString());
			smsData.setOtp(data.getString("amount").toString());
		} catch (JSONException e) {
			System.err.println("ERROR IN PARSING DATA :- "+e);
		}
		return smsData;
	}
	JSONObject generateResponse(JSONObject jsonObject,StatusPOJO status) throws JSONException{
		jsonObject.put("STATUS", status.getStatus());
		jsonObject.put("MESSAGE", status.getMessage());
		return jsonObject;
	}
	
	JSONObject generateSMSResponse(JSONObject jsonObject,SMSResponse smsResponse) throws JSONException{
		jsonObject.put("receiverContract", smsResponse.getReceiverContract());
		jsonObject.put("senderContract", smsResponse.getSenderContract());
		jsonObject.put("amt", smsResponse.getAmt());
		jsonObject.put("message", smsResponse.getMessage());
		return jsonObject;
	}
}
