package com.example.org.shardingjdbcmetadatatest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Locale;


@SpringBootApplication
@RestController
public class ShardingJdbcMetaDataTestApplication {
    
    @Autowired
    @Qualifier(value = "shardingSphereDataSource")
    private DataSource dataSource;
    
    @Value("${spring.profiles.active}")
    private String env;
    
    public static void main(String[] args) {
        SpringApplication.run(ShardingJdbcMetaDataTestApplication.class, args);
    }
    
    @GetMapping("/hello")
    public String sql(@RequestParam(value = "sql") String sql) {
        if (env.equals("h2")) {
            sql = sql.toUpperCase(Locale.ROOT);
        }
        StringBuffer stringBuffer1 = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        try {
            try (
                    Connection conn = dataSource.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                
                try {
                    if (sql.toLowerCase().startsWith("select")) {
                        ResultSet rs = ps.executeQuery();
                        ResultSetMetaData metaData = rs.getMetaData();
                        
                        for (int i = 1; i <= metaData.getColumnCount(); i++) {
                            String columnName = metaData.getColumnName(i);
                            stringBuffer1.append("--").append(columnName);
                        }
                        while (rs.next()) {
                            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                                Object value = rs.getObject(i);
                                stringBuffer2.append("--").append(value);
                            }
                        }
                        
                    } else {
                        ps.execute();
                    }
                } finally {
                
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return stringBuffer1.length() == 0 ? "success" : stringBuffer1.append("\n").append(stringBuffer2).toString();
    }
    
}
