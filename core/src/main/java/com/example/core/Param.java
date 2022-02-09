package com.example.core;

public class Param {
    private String sql;
    private Object[] params;
    
    public String getSql() {
        return sql;
    }
    
    public void setSql(final String sql) {
        this.sql = sql;
    }
    
    public Object[] getParams() {
        return params;
    }
    
    public void setParams(final Object[] params) {
        this.params = params;
    }
}
