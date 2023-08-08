package com.light.ddbquery.utils;

import com.light.ddbquery.domain.DatabaseInfo;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

@Slf4j
public class ConnectionUtil {

    private ConnectionUtil() {
        throw new IllegalStateException("Utility class");
    }


    public static SQLUtil getSQLUtil(DatabaseInfo info) throws SQLException {
        String dbUrl = null;
        String tableSql = null;
        String columnSql = null;
        if ("MYSQL".equalsIgnoreCase(info.getType())) {
            dbUrl = "jdbc:mysql://" + info.getIp() + ":" + info.getPort() + "/" + info.getInstance() + "?serverTimezone=UTC";
            tableSql = "select table_name from information_schema.tables where TABLE_SCHEMA='" + info.getInstance() + "' order by table_name asc";
            columnSql = "select COLUMN_NAME from information_schema.COLUMNS where TABLE_SCHEMA='" + info.getInstance() + "' and table_name ='%s' order by COLUMN_NAME asc";
        } else if ("MARIADB".equalsIgnoreCase(info.getType())) {
            dbUrl = "jdbc:mariadb://" + info.getIp() + ":" + info.getPort() + "/" + info.getInstance();
        } else if ("ORACLE".equalsIgnoreCase(info.getType())) {
            dbUrl = "jdbc:oracle:thin:@" + info.getIp() + ":" + info.getPort() + ":" + info.getInstance();
            tableSql = "select table_name from user_tables order by table_name";
            columnSql = "select COLUMN_NAME from user_tab_columns where TABLE_NAME = upper('%s') order by COLUMN_NAME";
        } else if ("SQLSERVER".equalsIgnoreCase(info.getType())) {
            dbUrl = "jdbc:sqlserver://" + info.getIp() + ":" + info.getPort() + ";databaseName=" + info.getInstance();
        } else if ("POSTGRESQL".equalsIgnoreCase(info.getType())) {
            dbUrl = "jdbc:postgresql://" + info.getIp() + ":" + info.getPort() + "/" + info.getInstance();
        } else if ("DB2".equalsIgnoreCase(info.getType())) {
            dbUrl = "jdbc:db2:" + info.getIp() + ":" + info.getPort() + "/" + info.getInstance();
        } else if ("SYBASE".equalsIgnoreCase(info.getType())) {
            dbUrl = "jdbc:sybase:Tds:" + info.getIp() + ":" + info.getPort() + "/" + info.getInstance();
        }
        if (dbUrl != null && tableSql != null) {
            return new SQLUtil(info, dbUrl, tableSql, columnSql);
        } else if (dbUrl != null) {
            return new SQLUtil(info, dbUrl);
        }
        throw new SQLException("不支持的数据库连接");
    }
}