package com.example.encrypt;

import com.example.core.Param;
import com.example.core.execute.PrepareExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SpringBootApplication
@RestController
public class EncryptTestApp {
    @Autowired
    @Qualifier(value = "shardingSphereDataSource")
    private DataSource dataSource;
    
    public static void main(String[] args) {
        SpringApplication.run(EncryptTestApp.class, args);
    }
    
    @PostMapping("/execute")
    public String sql(@RequestBody Param param) throws SQLException {
        StringBuffer stringBuffer1 = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(param.getSql())) {
            new PrepareExecutor(ps, param.getParams()).simpleExecute(stringBuffer1, stringBuffer2);
            return stringBuffer1.length() == 0 ? "success" : stringBuffer1.append("\n").append(stringBuffer2).toString();
        }
    }
}
