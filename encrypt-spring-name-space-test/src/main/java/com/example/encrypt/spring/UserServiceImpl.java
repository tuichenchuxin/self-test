package com.example.encrypt.spring;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;

@Service("encrypt")
public class UserServiceImpl {
    
    @Resource
    private UserRepository userRepository;
    
    public void run () throws SQLException {
        userRepository.createTable();
        userRepository.alterAddColumn();
        userRepository.alterChangeColumn();
        userRepository.dropTable();
        userRepository.createTableWithPwd();
        userRepository.dropTable();
    }
    
}
