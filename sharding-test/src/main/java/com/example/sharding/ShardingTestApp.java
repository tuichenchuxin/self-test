package com.example.sharding;

import com.example.core.execute.PrepareExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.core.Param;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Locale;


@SpringBootApplication
@RestController
public class ShardingTestApp {
    
    @Autowired
    @Qualifier(value = "shardingSphereDataSource")
    private DataSource dataSource;
    
    @Value("${spring.profiles.active}")
    private String env;
    
    public static void main(String[] args) {
        SpringApplication.run(ShardingTestApp.class, args);
    }
    
    @PostMapping("/hello")
    public String sql(@RequestBody Param param) throws SQLException {
        if (env.equals("h2")) {
            param.setSql(param.getSql().toUpperCase(Locale.ROOT));
        }
        StringBuffer stringBuffer1 = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(param.getSql())) {
            new PrepareExecutor(ps, param.getParams()).simpleExecute(stringBuffer1, stringBuffer2);
            return stringBuffer1.length() == 0 ? "success" : stringBuffer1.append("\n").append(stringBuffer2).toString();
        }
    }
    
    @PostMapping("/executeByJdbc")
    public String executeByJdbc(@RequestBody Param param) throws ClassNotFoundException, SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(param.getSql())) {
            StringBuffer stringBuffer1 = new StringBuffer();
            StringBuffer stringBuffer2 = new StringBuffer();
            new PrepareExecutor(preparedStatement, param.getParams()).simpleExecute(stringBuffer1, stringBuffer2);
            return stringBuffer1.length() == 0 ? "success" : stringBuffer1.append("\n").append(stringBuffer2).toString();
        }
    }
    
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/demo_ds_0?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&allowMultiQueries=true";
        return DriverManager.getConnection(url, "root", "123456");
    }
    
}
