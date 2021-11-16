package com.example.org.shardingjdbcmetadatatest;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class MonthShardingTableAlgorithm<T extends Comparable<?>> implements PreciseShardingAlgorithm<T>, RangeShardingAlgorithm<T> {
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<T> preciseShardingValue) {
        int startMonth = 0;
        if (preciseShardingValue.getValue() instanceof Date) {
            Date date = (Date) preciseShardingValue.getValue();
            startMonth = date.getMonth() + 1;
        } else if (preciseShardingValue.getValue() instanceof String) {
            String dateStr = (String) preciseShardingValue.getValue();
            String[] array = dateStr.split(" ");
            String[] date = array[0].split("-");
            startMonth = Integer.parseInt(date[1]);
        }

        String table = "";
        for (String each : collection) {
            String[] arr = each.split("_");
            int tableIndex = Integer.parseInt(arr[arr.length - 1]);
            if (tableIndex == startMonth) {
                table = each;
                break;
            }
        }
        return table;
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<T> rangeShardingValue) {
        Range<T> valueRange = rangeShardingValue.getValueRange();//获得输入的查询条件范围

        Object startDate = new Object();
        Object endDate = new Object();
        int startMonth = 0;
        int endMonth = 0;


        //上下限查询条件都有
        if (valueRange.hasLowerBound() && valueRange.hasUpperBound()) {
            startDate = valueRange.lowerEndpoint();
            endDate = valueRange.upperEndpoint();

            //只有下限或只有上限
        } else if (valueRange.hasLowerBound()) {
            startDate = valueRange.lowerEndpoint();
            endDate = valueRange.lowerEndpoint();
        } else if (valueRange.hasUpperBound()) {
            startDate = valueRange.hasUpperBound();
            endDate = valueRange.hasUpperBound();
        }

        if (startDate instanceof Date) {
            Date start = (Date) startDate;
            Date end = (Date) endDate;
            startMonth = start.getMonth() + 1;
            endMonth = end.getMonth() + 1;

        } else if (startDate instanceof String) {
            String start = (String) startDate;
            String end = (String) endDate;
            String[] array = start.split(" ");
            String[] date = array[0].split("-");
            startMonth = Integer.parseInt(date[1]);
            String[] arrayend = end.split(" ");
            String[] dateend = arrayend[0].split("-");
            endMonth = Integer.parseInt(dateend[1]);
        }


        Collection<String> collect = new ArrayList<>();
        for (String each : collection) {
            String[] arr = each.split("_");
            int tableIndex = Integer.parseInt(arr[arr.length - 1]);
            if (tableIndex >= startMonth && tableIndex <= endMonth) {
                if (!collect.contains(each)) {
                    collect.add(each);
                }
            }
        }
        return collect;
    }

}
