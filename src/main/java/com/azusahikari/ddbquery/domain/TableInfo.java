package com.azusahikari.ddbquery.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@Table(name = "table_info", uniqueConstraints = {@UniqueConstraint(name = "app_name_env", columnNames = {"app_name", "env"})})
public class TableInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "app_name", nullable = false)
    private String appName;
    @Column(nullable = false)
    private String env;
    private String tableAndColumn;

    public TableInfo(String appName, String env, String tableAndColumn) {
        this.appName = appName;
        this.env = env;
        this.tableAndColumn = tableAndColumn;
    }
}
