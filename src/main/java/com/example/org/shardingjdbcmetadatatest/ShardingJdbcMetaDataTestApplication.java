package com.example.org.shardingjdbcmetadatatest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
public class ShardingJdbcMetaDataTestApplication {
    
    @Autowired
    @Qualifier(value = "shardingSphereDataSource")
    private DataSource dataSource;
    
    @Value("${spring.profiles.active}")
    private String env;
    
    public static void main(String[] args) {
        SpringApplication.run(ShardingJdbcMetaDataTestApplication.class, args);
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
            simpleExecute(stringBuffer1, stringBuffer2, ps, param.getParams());
            return stringBuffer1.length() == 0 ? "success" : stringBuffer1.append("\n").append(stringBuffer2).toString();
        }
    }
    
    @PostMapping("/executeByJdbc")
    public String executeByJdbc(@RequestBody Param param) throws ClassNotFoundException, SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(param.getSql())) {
            StringBuffer stringBuffer1 = new StringBuffer();
            StringBuffer stringBuffer2 = new StringBuffer();
            simpleExecute(stringBuffer1, stringBuffer2, preparedStatement, param.getParams());
            return stringBuffer1.length() == 0 ? "success" : stringBuffer1.append("\n").append(stringBuffer2).toString();
        }
    }
    
    private void handleQueryResultSet(final StringBuffer stringBuffer1, final StringBuffer stringBuffer2, final ResultSet rs) throws SQLException {
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
    }
    
    private void simpleExecute(final StringBuffer stringBuffer1, final StringBuffer stringBuffer2, final PreparedStatement stmt, final Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        
        boolean hasMoreResultSets = stmt.execute();
        READING_QUERY_RESULTS:
        while (hasMoreResultSets || stmt.getUpdateCount() != -1) {
            if (hasMoreResultSets) {
                ResultSet rs = stmt.getResultSet();
                handleQueryResultSet(stringBuffer1, stringBuffer2, rs);
            } else { // if ddl/dml/...
                int queryResult = stmt.getUpdateCount();
                if (queryResult == -1) { // no more queries processed
                    break READING_QUERY_RESULTS;
                } // no more queries processedq
                // handle success, failure, generated keys, etc here
            } // if ddl/dml/...
            
            // check to continue in the loop
            hasMoreResultSets = stmt.getMoreResults();
        } // while results
    }
    
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/demo_ds_0?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&allowMultiQueries=true";
        return DriverManager.getConnection(url, "root", "123456");
    }
    
}
