package com.light.ddbquery.service.impl;

import com.light.ddbquery.domain.DatabaseInfo;
import com.light.ddbquery.repository.DatabaseInfoRepository;
import com.light.ddbquery.service.ExecuteSQLService;
import com.light.ddbquery.utils.ConnectionUtil;
import com.light.ddbquery.utils.SQLUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ExecuteSQLServiceImpl implements ExecuteSQLService {

    final DatabaseInfoRepository databaseInfoRepository;

    public ExecuteSQLServiceImpl(DatabaseInfoRepository databaseInfoRepository) {
        this.databaseInfoRepository = databaseInfoRepository;
    }


    @Override
    public Map<String, Object> executeSQL(Map<String, Object> inputMap) {
        String appName = (String) inputMap.get("appName");
        String env = (String) inputMap.get("env");
        String sql = (String) inputMap.get("sql");
        Integer cursor = (Integer) inputMap.get("cursor");
        Map<String, Object> result = new HashMap<>();
        if (cursor != null) {
            // 所有字符串去壳
            List<String> allBlock = splitSql(sql);
            // 获取子字符串
            String subSql = sql;
            if (sql.length() > cursor) {
                subSql = sql.substring(0, cursor + 1);
            }
            // 子字符串去壳
            List<String> allSubBlock = splitSql(subSql);
            sql = allBlock.get(allSubBlock.size() - 1);
        }
        String[] sqlList = sql.split(";");
        List<Map<String, Object>> results = new ArrayList<>();
        for (String s : sqlList) {
            if (s.trim().equals("")) {
                continue;
            }
            Pattern p = Pattern.compile("(?ms)('(?:''|[^'])*')|--.*?$|/\\*.*?\\*/|#.*?$|");
            s = p.matcher(s).replaceAll("$1").trim();
            if ("select".equalsIgnoreCase(s.substring(0, 6))) {
                Map<String, Object> sqlResult = query(appName, env, s);
                sqlResult.put("sql", s);
                sqlResult.put("mode", "select");
                results.add(sqlResult);
            } else if ("update".equalsIgnoreCase(s.substring(0, 6))) {
                Map<String, Object> sqlResult = update(appName, env, s);
                sqlResult.put("sql", s);
                sqlResult.put("mode", "update");
                results.add(sqlResult);
            } else if ("insert".equalsIgnoreCase(s.substring(0, 6))) {
                Map<String, Object> sqlResult = update(appName, env, s);
                sqlResult.put("sql", s);
                sqlResult.put("mode", "insert");
                results.add(sqlResult);
            }
        }
        result.put("results", results);
        return result;
    }

    private List<String> splitSql(String sql) {
        String[] sqlAll = sql.split("\n");
        StringBuilder sqlTrim = new StringBuilder();
        for (String per : sqlAll) {
            sqlTrim.append(per.trim()).append("\n");
        }
        sqlTrim.deleteCharAt(sqlTrim.length() - 1);
        String[] subAll = sqlTrim.toString().split("\n\n");
        List<String> sqlList = new ArrayList<>();
        for (String per : subAll) {
            if (!per.trim().equals("")) {
                sqlList.add(per.trim());
            }
        }
        return sqlList;
    }


    public Map<String, Object> query(String appName, String env, String sql) {
        Map<String, Object> outputMap = new HashMap<>();
        List<DatabaseInfo> infoList = databaseInfoRepository.findByAppNameAndEnv(appName, env);
        List<ResultSetMetaData> metaDataList = new ArrayList<>();
        List<List<Object>> rows = new ArrayList<>();
        for (DatabaseInfo info : infoList) {
            SQLUtil util = null;
            try {
                util = ConnectionUtil.getSQLUtil(info);
                ResultSet resultSet = util.query(sql);
                ResultSetMetaData metaData = resultSet.getMetaData();
                metaDataList.add(metaData);
                int columnCount = metaData.getColumnCount();
                rows.addAll(rows(info, resultSet, columnCount));
                resultSet.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                if (util != null) {
                    util.close();
                }
            }
        }
        outputMap.put("return_code", "0");
        outputMap.put("return_message", "调用成功");
        outputMap.put("head", head(metaDataList));
        outputMap.put("rows", rows);
        return outputMap;
    }


    public Map<String, Object> update(String appName, String env, String sql) {
        Map<String, Object> outputMap = new HashMap<>();
        List<DatabaseInfo> infoList = databaseInfoRepository.findByAppNameAndEnv(appName, env);
        List<String> heads = new ArrayList<>();
        heads.add("SET");
        heads.add("updateNum");
        List<List<Object>> rows = new ArrayList<>();
        for (DatabaseInfo info : infoList) {
            SQLUtil util = null;
            try {
                util = ConnectionUtil.getSQLUtil(info);
                int resultSet = util.update(sql);
                List<Object> row = new ArrayList<>();
                row.add(info.getSetName());
                row.add(resultSet);
                rows.add(row);
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                if (util != null) {
                    util.close();
                }
            }
        }
        outputMap.put("return_code", "0");
        outputMap.put("return_message", "调用成功");
        outputMap.put("head", heads);
        outputMap.put("rows", rows);
        return outputMap;
    }

    private List<List<Object>> rows(DatabaseInfo info, ResultSet resultSet, Integer columnCount) throws SQLException {
        List<List<Object>> rows = new ArrayList<>();
        while (resultSet.next()) {
            List<Object> row = new ArrayList<>();
            row.add(info.getSetName());
            for (int i = 1; i <= columnCount; i++) {
                if (resultSet.getString(i) != null) {
                    row.add(resultSet.getString(i));
                } else {
                    row.add("");
                }
            }
            rows.add(row);
        }
        return rows;
    }

    private List<String> head(List<ResultSetMetaData> metaDataList) {
        List<String> list = new ArrayList<>();
        ResultSetMetaData temp = metaDataList.get(0);
        try {
            int columnCount = temp.getColumnCount();
            list.add("SET");
            for (int i = 1; i <= columnCount; i++) {
                list.add(temp.getColumnName(i));
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return list;
    }
}
