package com.fis.resources.beans;

import java.util.Date;

public class TextDBData 
{
	private int Id ;          
	private Date ReceivedTime;  
	private Date SentTime;
	private String MessageFrom; 
	private String MessageText;
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public Date getReceivedTime() {
		return ReceivedTime;
	}
	public void setReceivedTime(Date receivedTime) {
		ReceivedTime = receivedTime;
	}
	
	public Date getSentTime() {
		return SentTime;
	}
	public void setSentTime(Date sentTime) {
		SentTime = sentTime;
	}
	public String getMessageFrom() {
		return MessageFrom;
	}
	public void setMessageFrom(String messageFrom) {
		MessageFrom = messageFrom;
	}
	public String getMessageText() {
		return MessageText;
	}
	public void setMessageText(String messageText) {
		MessageText = messageText;
	}
	
	
}
