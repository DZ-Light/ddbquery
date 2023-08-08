package com.light.ddbquery.repository;

import com.light.ddbquery.domain.TableInfo;
import org.springframework.data.repository.CrudRepository;

public interface TableInfoRepository extends CrudRepository<TableInfo, String> {
    TableInfo findByAppNameAndEnv(String appName, String env);
}
