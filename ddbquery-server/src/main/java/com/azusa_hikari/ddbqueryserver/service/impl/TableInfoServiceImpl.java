package com.azusa_hikari.ddbqueryserver.service.impl;

import com.azusa_hikari.ddbqueryserver.domain.DatabaseInfo;
import com.azusa_hikari.ddbqueryserver.domain.TableInfo;
import com.azusa_hikari.ddbqueryserver.repository.DatabaseInfoRepository;
import com.azusa_hikari.ddbqueryserver.repository.TableInfoRepository;
import com.azusa_hikari.ddbqueryserver.service.TableInfoService;
import com.azusa_hikari.ddbqueryserver.utils.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
public class TableInfoServiceImpl implements TableInfoService {

    final DatabaseInfoRepository dbInfoRepository;
    final TableInfoRepository tableInfoRepository;

    public TableInfoServiceImpl(DatabaseInfoRepository dbInfoRepository, TableInfoRepository tableInfoRepository) {
        this.dbInfoRepository = dbInfoRepository;
        this.tableInfoRepository = tableInfoRepository;
    }

    @Override
    public String getTableAndColumn(String appName, String env) {
        TableInfo tableInfo = tableInfoRepository.findByAppNameAndEnv(appName, env);
        if (tableInfo == null) {
            return updateTableAndColumn(appName, env);
        } else {
            return tableInfo.getTableAndColumn();
        }
    }

    @Override
    public String updateTableAndColumn(String appName, String env) {
        TableInfo tableInfo = tableInfoRepository.findByAppNameAndEnv(appName, env);
        List<DatabaseInfo> dbInfos = dbInfoRepository.findByAppNameAndEnv(appName, env);
        DatabaseInfo databaseInfo = dbInfos.iterator().next();
        String temp = null;
        try {
            temp = ConnectionUtil.getSQLUtil(databaseInfo).getTableAndColumn();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        if (tableInfo != null) {
            tableInfo.setTableAndColumn(temp);
        } else {
            tableInfo = new TableInfo(appName, env, temp);
        }
        tableInfoRepository.save(tableInfo);
        return temp;
    }

}
