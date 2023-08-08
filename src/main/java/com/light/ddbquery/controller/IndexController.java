package com.light.ddbquery.controller;


import com.light.ddbquery.domain.SqlInfo;
import com.light.ddbquery.repository.DatabaseInfoRepository;
import com.light.ddbquery.repository.SQLInfoRepository;
import com.light.ddbquery.service.ExecuteSQLService;
import com.light.ddbquery.service.TableInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class IndexController {
    final DatabaseInfoRepository dbInfoRepository;
    final SQLInfoRepository sqlInfoRepository;
    final TableInfoService tableInfoService;
    final ExecuteSQLService executeSQLService;

    public IndexController(DatabaseInfoRepository dbInfoRepository, SQLInfoRepository sqlInfoRepository, TableInfoService tableInfoService, ExecuteSQLService executeSQLService) {
        this.dbInfoRepository = dbInfoRepository;
        this.sqlInfoRepository = sqlInfoRepository;
        this.tableInfoService = tableInfoService;
        this.executeSQLService = executeSQLService;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        String appName = (String) session.getAttribute("appName");
        List<String> appNameList = dbInfoRepository.queryAllAppName();
        model.addAttribute("appNameList", appNameList);
        model.addAttribute("appName", appName);
        return "index";
    }

    @PostMapping("/getAppInfo")
    @ResponseBody
    public Map<String, Object> getAppInfo(@RequestBody Map<String, Object> requestMap, HttpSession session) {
        log.info("getAppInfo 入参:{}", requestMap);
        String appName = (String) requestMap.get("appName");
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("envs", dbInfoRepository.queryEnvByAppName(appName));
        responseMap.put("sqlInfos", sqlInfoRepository.findByAppName(appName));
        session.setAttribute("appName", appName);
        log.info("getAppInfo 出参:{}", responseMap);
        return responseMap;
    }

    @PostMapping("/getTableAndColumn")
    @ResponseBody
    public Map<String, Object> getTableAndColumn(@RequestBody Map<String, Object> requestMap) {
        log.info("getTableAndColumn 入参:{}", requestMap);
        String appName = (String) requestMap.get("appName");
        String env = (String) requestMap.get("env");
        String model = (String) requestMap.get("model");
        Map<String, Object> responseMap = new HashMap<>();
        if ("query".equals(model)) {
            responseMap.put("tableAndColumn", tableInfoService.getTableAndColumn(appName, env));
        } else {
            responseMap.put("tableAndColumn", tableInfoService.updateTableAndColumn(appName, env));
        }

        log.info("getTableAndColumn 出参:{}", responseMap);
        return responseMap;
    }

    @PostMapping("/saveSqlInfo")
    @ResponseBody
    public Map<String, Object> saveSqlInfo(@RequestBody Map<String, Object> requestMap) {
        log.info("saveSqlInfo 入参:{}", requestMap);
        String appName = (String) requestMap.get("appName");
        String sqlId = (String) requestMap.get("sqlId");
        String sql = (String) requestMap.get("sql");
        Integer selected = null;
        // 新增或更新脚本
        if (!sql.equals("")) {
            SqlInfo temp = sqlInfoRepository.save(new SqlInfo(sqlId.equals("") ? null : Integer.parseInt(sqlId), appName, sql));
            selected = temp.getId();
        }
        // 删除脚本
        if (!sqlId.equals("") && sql.equals("")) {
            sqlInfoRepository.delete(new SqlInfo(Integer.parseInt(sqlId), appName, sql));
        }
        List<SqlInfo> sqlInfos = sqlInfoRepository.findByAppName(appName);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("sqlInfos", sqlInfos);
        responseMap.put("selected", selected);
        log.info("saveSqlInfo 出参:{}", responseMap);
        return responseMap;
    }

    @PostMapping("/getSqlById")
    @ResponseBody
    public Map<String, Object> getSqlById(@RequestBody Map<String, Object> requestMap) {
        log.info("getSqlById 入参:{}", requestMap);
        String sqlId = (String) requestMap.get("sqlId");
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("sql", sqlInfoRepository.findById(Integer.parseInt(sqlId)).getSql());
        log.info("getSqlById 出参:{}", responseMap);
        return responseMap;
    }

    @PostMapping("/executeSQL")
    @ResponseBody
    public Map<String, Object> executeSQL(@RequestBody Map<String, Object> requestMap) {
        log.info("executeSQL 入参:{}", requestMap);
        Map<String, Object> responseMap = executeSQLService.executeSQL(requestMap);
        log.info("executeSQL 出参:{}", responseMap);
        return responseMap;
    }
}
