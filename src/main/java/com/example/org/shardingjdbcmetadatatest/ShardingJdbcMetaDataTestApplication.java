package com.example.org.shardingjdbcmetadatatest;

import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class ShardingJdbcMetaDataTestApplication {

	public static void main(String[] args) throws InterruptedException {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(ShardingJdbcMetaDataTestApplication.class, args);
		applicationContext.getBean(ShardingSphereDataSource.class);
		while(true) {
			Thread.sleep(1000);
		}
	}

}
