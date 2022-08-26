package com.azusa_hikari.ddbqueryserver.repository;

import com.azusa_hikari.ddbqueryserver.domain.TableInfo;
import org.springframework.data.repository.CrudRepository;

public interface TableInfoRepository extends CrudRepository<TableInfo, String> {
    TableInfo findByAppNameAndEnv(String appName, String env);
}
