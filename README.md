# BigData
This is to connect to Hbase via phoenix.

versions which is used.

hbase and phoenix version used 
4.8.1-HBase-1.1

setup hbase
download phoenix "apache-phoenix-4.8.1-HBase-1.1-bin"

Add hbase-sitex.xml with 
=========================

<configuration>
  <property>
    <name>hbase.regionserver.wal.codec</name>
    <value>org.apache.hadoop.hbase.regionserver.wal.IndexedWALEditCodec</value>
  </property>
    <property>
      <name>phoenix.schema.isNamespaceMappingEnabled</name>
      <value>true</value>
    </property>
    <property>
      <name>phoenix.schema.mapSystemTablesToNamespace</name>
      <value>true</value>
    </property>
</configuration>


Please note the pom.xml where am excluding servlet-api and log4j 
which were conflicting with spring-boot depenency.

login to phoenix and create some data
C>cd apache-phoenix-4.8.1-HBase-1.1-bin/bin
C> ./sqlline.py ZkUrl:2181:/hbase-unsecure
C> CREATE TABLE IF NOT EXISTS USER (
      ID VARCHAR NOT NULL primary key,
      NAME VARCHAR NULL);
C> 	  UPSERT INTO USER VALUES('1','Naveen');
	  UPSERT INTO USER VALUES('2','Harsha');
	  UPSERT INTO USER VALUES('3','Raghu');

C> 	  select * from USER;
+-----+---------+
| ID  |  NAME   |
+-----+---------+
| 1   | Naveen  |
| 2   | Harsha  |
| 3   | Raghu   |
+-----+---------+

Run the spring-boot application and hit the url

http://localhost:8080/hbase/user


You should see resonse as
=======================

[
{
"name": "Naveen",
"id": "1"
},
{
"name": "Harsha",
"id": "2"
},
{
"name": "Raghu",
"id": "3"
}
]
