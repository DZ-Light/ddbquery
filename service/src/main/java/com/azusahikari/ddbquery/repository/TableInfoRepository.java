package com.azusahikari.ddbquery.repository;

import com.azusahikari.ddbquery.domain.TableInfo;
import org.springframework.data.repository.CrudRepository;

public interface TableInfoRepository extends CrudRepository<TableInfo, String> {
    TableInfo findByAppNameAndEnv(String appName, String env);
}
