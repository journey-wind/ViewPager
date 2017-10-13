package com.example.Record;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.example.viewpagertest.R;

import android.content.Context;


public class SqlUtil {

	private Context context;
	private Connection con;

	public void DbInit(){
		String url=context.getString(R.string.dbURL);
		String name=context.getString(R.string.dbName);
		String password=context.getString(R.string.passWord);
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
        try {
			con = DriverManager.getConnection(url, name, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
	}
}
