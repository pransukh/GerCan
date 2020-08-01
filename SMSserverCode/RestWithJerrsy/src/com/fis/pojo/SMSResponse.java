package com.fis.pojo;

public class SMSResponse {
	private String receiverContract;
	private String senderContract;
	private String amt;
	private String message;
	public String getReceiverContract() {
		return receiverContract;
	}
	public void setReceiverContract(String receiverContract) {
		this.receiverContract = receiverContract;
	}
	public String getSenderContract() {
		return senderContract;
	}
	public void setSenderContract(String senderContract) {
		this.senderContract = senderContract;
	}
	public String getAmt() {
		return amt;
	}
	public void setAmt(String amt) {
		this.amt = amt;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
