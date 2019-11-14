package com.e2open;

import java.sql.*;
import java.util.Properties;

public class PhoenixJavaTest {



    public static void main(String[] args) {

        String zkUrl= "172.16.2.144,172.16.2.146:2181:/hbase-unsecure";//"172.16.4.40:2181";
        String driver = "org.apache.phoenix.jdbc.PhoenixDriver";
        String user  = null;
        String password  = null;
        String autoCommit =";autocommit=true";
        String jdbcUrl  = "jdbc:phoenix:" + zkUrl;
        //private Connection connection  = null ;
        //private Statement statement  = null ;

        Connection con = null;
        try {
            Class.forName(driver);
            Properties info = new Properties();
            info.put("phoenix.schema.isNamespaceMappingEnabled", "true");
            info.put("hbase.client.scanner.timeout.period", "600000");
            //info.put("zookeeper.znode.parent", "/hbase-unsecure");
            Connection connection = DriverManager.getConnection(jdbcUrl, info);
            //Statement statement = connection.createStatement();

            String sql = "SELECT COUNT(1) COUNT FROM QA_CRP_V_TEST1.INV_FISCAL_DAY_POSITIONS";//"SELECT * FROM CRP_313.INV_FISCAL_DAY_POSITIONS";
            //statement.addBatch(sql);


            //con = DriverManager.getConnection(databaseUri);

            //statement.executeBatch();
            //connection.commit()

            PreparedStatement ps = connection.prepareStatement(sql);


            ResultSet res = ps.executeQuery();


            while (res.next()) {
                System.out.println("COUNT IS " + res.getString("COUNT"));
            }

            //System.out.print("close");
            res.close();
            ps.close();
            connection.close();


        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("Connection fail: " + e);
        } catch (Exception e){
            e.printStackTrace();
            System.out.print("Connection fail 2: " + e);
        }

        //dataSource.setDriverClassName("org.apache.phoenix.jdbc.PhoenixDriver");
        System.out.println("Initialized hbase");
    }
}