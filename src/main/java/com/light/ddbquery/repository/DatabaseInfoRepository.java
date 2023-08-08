package com.light.ddbquery.repository;

import com.light.ddbquery.domain.DatabaseInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DatabaseInfoRepository extends CrudRepository<DatabaseInfo, String> {
    List<DatabaseInfo> findByAppNameAndEnv(String appName, String env);

    @Query("select distinct appName from DatabaseInfo order by appName")
    List<String> queryAllAppName();

    @Query("select distinct env from DatabaseInfo where appName = :appName order by env desc ")
    List<String> queryEnvByAppName(@Param("appName") String appName);
}
