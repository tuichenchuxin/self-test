package com.example.elasticjob;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;

public class MyJob implements SimpleJob {
    @Override
    public void execute(final ShardingContext shardingContext) {
        System.out.println("--------------- start my job -------------");
        System.out.println("--------------- end my job -------------");
    }
}
