package com.azusa_hikari.ddbqueryserver.domain;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "database_info", uniqueConstraints = {@UniqueConstraint(name = "env_set_name", columnNames = {"env", "set_name"})})
public class DatabaseInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String env;
    @Column(nullable = false)
    private String appName;
    @Column(name = "set_name", nullable = false)
    private String setName;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private String ip;
    @Column(nullable = false)
    private String port;
    @Column(nullable = false)
    private String instance;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
}
