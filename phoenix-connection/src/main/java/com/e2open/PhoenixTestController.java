package com.e2open;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/hbase")
public class PhoenixTestController {

	@Autowired
	private HbaseDataSource hbaseDataSource;
	

	public HbaseDataSource getHbaseDataSource() {
		return hbaseDataSource;
	}

	public void setHbaseDataSource(HbaseDataSource hbaseDataSource) {
		this.hbaseDataSource = hbaseDataSource;
	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
    public List<User> getPartnersWithDelta() throws Exception{
			
	        List<User> users = new ArrayList<User>();
	        
	        Connection connection = null;
	        try {
	        	connection = hbaseDataSource.getConnection();
	        	String sql = "select * from USER";
	        	PreparedStatement ps = connection.prepareStatement(sql);
	            ResultSet res = ps.executeQuery();
	            while (res.next()) {
	            	User user = new User();
	            	user.setId(res.getString("ID"));
	            	user.setName(res.getString("NAME"));
	            	users.add(user);
	            }
	            res.close();
	            ps.close();
		        System.out.println("Initialized hbase");

	        } catch (SQLException e) {
	            e.printStackTrace();
	            System.out.print("Connection fail: " + e);
	        } catch (Exception e){
	            e.printStackTrace();
	            System.out.print("Connection fail 2: " + e);
	        }finally {
	        	if(null!=connection) {
	        		hbaseDataSource.releaseConnection(connection);
	        	}
			}

	        return users;
    }

}
