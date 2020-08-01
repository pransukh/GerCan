package com.fis.demo;

import java.util.Iterator;

import org.bson.Document;
import org.json.JSONObject;

import com.fis.pojo.MemberPOJO;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class DBConnection 
{
	/*
	 * Recommended to keep in property file.
	 * */
	private String collectionName = "";
	private String dbURI = "";
	private int dbPort = 0 ;
	private String dbName = "";
	private String dbUserName = "";
	private String dbPassword = "";
	DBConnection(){
		collectionName = "FIS_MEM_COLLECTION";
		dbURI = "localhost";
		dbPort = 27017;
		dbName = "FIS_DB";
		dbUserName = "e5552323";
		dbPassword = "FIS@123";
	}
	boolean sendDataToDB(MongoDatabase database,MemberPOJO memberPOJO)
	{
		MongoCollection<Document> collection = database.getCollection(collectionName); 
		System.out.println("Collection FIS_MEM_COLLECTION selected successfully");

		FindIterable<Document> collectionDOC = collection.find();
		
		if(collectionDOC !=null)
		{
			Iterator itObj=collectionDOC.iterator();
			while(itObj.hasNext()){
				System.out.println(itObj.next());
			}
		}
		else{

			database.createCollection(collectionName); 
			System.out.println("Collection FIS_MEM_COLLECTION created successfully");

			Document document = new Document("title", "MongoDB") 
			.append("id", memberPOJO.getName())
			.append("description", memberPOJO.getBankAcc()) ;
			collection.insertOne(document); 
			System.out.println("Document inserted in collection 'FIS_MEM_COLLECTION' successfully");  

		}
		return true;
	}
	MongoDatabase mongoDBConnection()
	{
		System.out.println("IN mongoDBConnection()");
		MongoClient mongo = new MongoClient( dbURI , dbPort ); 

		// Creating Credentials 
		MongoCredential credential; 
		credential = MongoCredential.createCredential(dbUserName, dbName,dbPassword.toCharArray()); 
		System.out.println("Connected to the database successfully");  

		// Accessing the database 
		MongoDatabase database = mongo.getDatabase(dbName); 
		
		System.out.println("Credentials ::"+ credential); 
		return database;
	}
	boolean insertMemeber(JSONObject data){

		System.out.println("in insertMemeber() ");
		Boolean status = null;
		MemberPOJO memberPOJO=new JSONObjectBuilder().jsonParser(data,new MemberPOJO());
		MongoDatabase database = mongoDBConnection();
		if( database != null){
			if(sendDataToDB(database,memberPOJO))
				status = true;
			else
				status = false;
		}
		return status;
	}
}
