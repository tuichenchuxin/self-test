package com.example.encrypt.spring;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.SQLException;

public class EncryptSpringTestApp {
    private static final String CONFIG_FILE = "META-INF/application-encrypt-databases.xml";
    public static void main(String[] args) throws SQLException {
        try (ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONFIG_FILE)) {
            applicationContext.getBean("encrypt", UserServiceImpl.class).run();
        }
    }
}
