package com.example.core.execute;

import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

@RequiredArgsConstructor
public final class PrepareExecutor {
    
    private final PreparedStatement stmt; 
    
    private final Object[] params;
    
    public void simpleExecute(final StringBuffer stringBuffer1, final StringBuffer stringBuffer2) throws SQLException {
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
    
}
