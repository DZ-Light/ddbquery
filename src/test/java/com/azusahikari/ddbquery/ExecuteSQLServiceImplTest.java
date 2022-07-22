package com.azusahikari.ddbquery;

import com.azusahikari.ddbquery.service.ExecuteSQLService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest
class ExecuteSQLServiceImplTest {
    @Autowired
    ExecuteSQLService executeSQLService;

    @Test
    void contextLoads() {
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("appName", "test");
        inputMap.put("env", "dev");
        inputMap.put("sql", "select * from user;\n\n\n   select * from user; \n\n\n --user \n select * from user;\nUPDATE user SET name = 'txy' WHERE id = 2");
        inputMap.put("cursor", 65);
        Map<String, Object> result = executeSQLService.executeSQL(inputMap);
        log.info(String.valueOf(result));
    }

}
