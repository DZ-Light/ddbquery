package com.azusa_hikari.ddbqueryserver.service;

import java.util.Map;

public interface ExecuteSQLService {
    Map<String, Object> executeSQL(Map<String, Object> inputMap);

}
