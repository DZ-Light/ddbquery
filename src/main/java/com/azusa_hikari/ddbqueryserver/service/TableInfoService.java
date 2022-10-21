package com.azusa_hikari.ddbqueryserver.service;

public interface TableInfoService {

    String getTableAndColumn(String appName, String env);

    String updateTableAndColumn(String appName, String env);
}
