package com.github.wuudongdong.week07.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {
    public static ThreadLocal<String> LOOKUP_KEY_HOLDER = new ThreadLocal<>();

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceId;
        try {
            dataSourceId = LOOKUP_KEY_HOLDER.get();
            if (dataSourceId != null) {
                log.debug("线程[{}]，此时切换到的数据源为:{}", Thread.currentThread().getId(), dataSourceId);
            }
        } finally {
            LOOKUP_KEY_HOLDER.remove();
        }
        return dataSourceId;
    }
}
