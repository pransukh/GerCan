package com.fis.demo;




import java.io.InputStream;






import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;






import org.json.JSONObject;

import com.fis.pojo.MemberPOJO;
import com.fis.pojo.SMSData;
import com.fis.pojo.SMSResponse;
import com.fis.pojo.StatusPOJO;


// Plain old Java Object it does not extend as class or implements
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /member
@Path("/member")
public class WebService  {

	
	JSONObject resJsonObj = null;
	StatusPOJO status = null;
	JSONObjectBuilder jsonObjectBuilder = null;
	public WebService() {
		
		 resJsonObj = new JSONObject();
		 status = new StatusPOJO();
		 jsonObjectBuilder = new JSONObjectBuilder();
	}
	// This method is called if TEXT_PLAIN is request
	@Path("/addMember")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addMember(InputStream requestData) throws Exception{

		System.out.println("in addMember()");


		
		if(new DBConnection().insertMemeber(jsonObjectBuilder.getJsonData(requestData))){
			
			status.setStatus("SUCCESS");
			status.setMessage("MEMBER INSERTED SUCCESSFULLY !!");
			resJsonObj = jsonObjectBuilder.generateResponse(resJsonObj,status);
			
		}
		else{
			status.setStatus("ERROR");
			status.setMessage("!! EXCEPTION OCCURED !!");
			resJsonObj = jsonObjectBuilder.generateResponse(resJsonObj,status);

		}
		System.out.println("RESPONSE :- "+ resJsonObj);
		return Response.status(200).entity(resJsonObj.toString()).build();

	}

	@Path("/getMember/{id}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMember(@PathParam("id") String memID){

		System.out.println("Received memID:-"+memID);
		/*
		 * PENDING OPERATION
		 */
		return null;
	}

	@Path("/simpleMessageService")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response simpleMessageService(InputStream requestData) throws Exception {

		SMSData smsData = new JSONObjectBuilder().jsonParserSMS(jsonObjectBuilder.getJsonData(requestData),new SMSData());
		System.out.println("Data Received Receiver:- "+smsData.getReceiverContract());
		System.out.println("Data Received Sender:- "+smsData.getSenderContract());
		System.out.println("Data Received OTP:- "+smsData.getOtp());
		
		SMSResponse smsResponse = new SMSResponse();
		smsResponse.setReceiverContract(smsData.getReceiverContract());
		smsResponse.setSenderContract(smsData.getSenderContract());
		smsResponse.setAmt("401");
		smsResponse.setMessage("Payment successfull.");
		
		jsonObjectBuilder.generateSMSResponse(resJsonObj, smsResponse);
		
		/*MemberPOJO memberPOJO = new JSONObjectBuilder().jsonParser(jsonObjectBuilder.getJsonData(requestData),new MemberPOJO());
		System.out.println("Data Received Name:- "+memberPOJO.getName());
		System.out.println("Data Received ACCO:- "+memberPOJO.getBankAcc());*/
		
		/*status.setStatus("SUCCESS");
		status.setMessage("PAYMENT DONE SUCCESSFULLY !!");
		resJsonObj = jsonObjectBuilder.generateResponse(resJsonObj,status);
		System.out.println("RESPONSE :- "+ resJsonObj);*/
		
		return Response.status(200).entity(resJsonObj.toString()).build();
	}


	
	
	
	
	

	

}