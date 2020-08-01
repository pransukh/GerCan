package com.fis.sms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnection {

	public static DBConnection instance = null;
	private Connection con = null;
	static boolean wasDBConnNull = true;
	private DBConnection(){}
	
	public static synchronized DBConnection getInstance(){
		if(instance==null){
			instance = new DBConnection();
			wasDBConnNull = true;
		}else{
			wasDBConnNull = false;
		}
		
		return instance;
	}
	
	
	public Connection getDBConnection(String driverString, String dburl,String username, String password)
	{
		try {
			Class.forName(driverString);
			con=DriverManager.getConnection(dburl,username,password);  
			System.out.println("Connection Successfull.");
			//Statement stmt=con.createStatement();  
			//ResultSet rs=stmt.executeQuery("select * from emp"); 
		} 
		catch (Exception e) 
		{
			System.err.println("!!!!!!! Connection Not Initialized !!!!!!!");
			e.printStackTrace();
		}
		return con;
	}
	
}
