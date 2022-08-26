package com.azusa_hikari.ddbqueryserver.utils;

import com.azusa_hikari.ddbqueryserver.domain.DatabaseInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SQLUtil {
    DatabaseInfo dbInfo;
    protected String tableSql;
    protected String columnSql;
    Connection connection;
    Statement statement;

    protected SQLUtil(DatabaseInfo dbInfo, String dbUrl) throws SQLException {
        this.dbInfo = dbInfo;
        connection = DriverManager.getConnection(dbUrl, dbInfo.getUsername(), dbInfo.getPassword());
        statement = connection.createStatement();
    }

    protected SQLUtil(DatabaseInfo dbInfo, String dbUrl, String tableSql, String columnSql) throws SQLException {
        this.dbInfo = dbInfo;
        connection = DriverManager.getConnection(dbUrl, dbInfo.getUsername(), dbInfo.getPassword());
        statement = connection.createStatement();
        this.tableSql = tableSql;
        this.columnSql = columnSql;
    }

    public String getTableAndColumn() throws SQLException {
        if (tableSql == null || columnSql == null) {
            throw new SQLException("该数据库暂不支持获取表名和列名");
        }
        String result = "";
        try {
            ResultSet resultSet = query(tableSql);
            List<String> tables = new ArrayList<>();
            Map<String, Object> tableAndColumn = new HashMap<>();
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
            for (String table : tables) {
                List<String> columns = new ArrayList<>();
                resultSet = query(String.format(columnSql, table)); // 执行查询语句，返回结果数据集
                while (resultSet.next()) {
                    columns.add(resultSet.getString(1));
                }
                tableAndColumn.put(table, columns);
            }
            result = new ObjectMapper().writeValueAsString(tableAndColumn);
            // 关闭连接
            resultSet.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            close();
        }
        return result;
    }

    public ResultSet query(String sql) throws SQLException {
        return statement.executeQuery(sql);
    }

    public int update(String sql) throws SQLException {
        return statement.executeUpdate(sql);
    }

    public void close() {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.error("statement关闭失败");
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("connection关闭失败");
            }
        }
    }
}
