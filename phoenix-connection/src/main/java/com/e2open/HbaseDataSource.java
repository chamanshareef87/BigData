package com.e2open;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HbaseDataSource {
	
	@Value("${hbase.phoenix.driverClass}")
    private String driverClass; //="org.apache.phoenix.jdbc.PhoenixDriver";
	@Value("${hbase.zkUrl}")
    private String zkUrl;
	@Value("${hbase.phoenixUrl.prefix}")
    private String phoenixUrlPrefix;
	@Value("${hbase.username}")
    private String username;
	@Value("${hbase.password}")
    private String password;
	@Value("${hbase.connections.min}")
    private int min; // The number of connections of initial pool
	@Value("${hbase.connections.max}")
    private int max;
    private int used; // The number of connections of polled and not released from poll
    private Queue<Connection> pool = new ConcurrentLinkedQueue<Connection>();
    
    private String jdbcUrl = phoenixUrlPrefix+zkUrl;
    private Properties info =  new Properties();

    @PostConstruct
    private void init() {
        try {

        	jdbcUrl = phoenixUrlPrefix+zkUrl;
        	info.put("phoenix.schema.isNamespaceMappingEnabled", "true");
        	info.put("hbase.client.scanner.timeout.period", "600000");

        	Class.forName(driverClass);
            for (int i = 0; i < min; i++) {
            	long time = System.currentTimeMillis();
            	Connection conn = DriverManager.getConnection(jdbcUrl, info);
                System.out.println("Time taken to get connection:::"+ (System.currentTimeMillis()-time) +"MilSec");
                pool.add(conn);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        System.out.println("Connection pool size::"+pool.size());
    }

    public Connection getConnection() {
    	System.out.println("Get a connetions");
        Connection conn  = null;
        if (pool.size() > 0) {
            conn = pool.poll();
            Thread connGen = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                    	long time = System.currentTimeMillis();
                        Connection conn = DriverManager.getConnection(jdbcUrl, info);
                        System.out.println("Time taken to get connection:::"+ (System.currentTimeMillis()-time) +"MilSec");
                        pool.add(conn);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
            connGen.start();
            used ++;
            System.out.println("Returning from pool");
        } else if(used < max) {
            try {
            	long time = System.currentTimeMillis();
                conn = DriverManager.getConnection(jdbcUrl, info);
                System.out.println("Time taken to get connection:::"+ (System.currentTimeMillis()-time) +"MilSec");
                System.out.println("Creating new connection as pool is empty");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Currently used connections::"+used+", connections in the pool::"+pool.size());
        return conn;
    }

    public void releaseConnection(Connection conn) {
        System.out.println("Releasing connetion:: "+conn);
    	if (conn != null) {
            try {
                conn.close();
                used--;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Currently used connections::"+used+", connections in the pool::"+pool.size());
    }

}
