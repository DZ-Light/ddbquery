package com.azusa_hikari.ddbqueryserver.repository;

import com.azusa_hikari.ddbqueryserver.domain.SqlInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SQLInfoRepository extends CrudRepository<SqlInfo, String> {
    List<SqlInfo> findByAppName(String appName);

    SqlInfo findById(Integer id);
}
