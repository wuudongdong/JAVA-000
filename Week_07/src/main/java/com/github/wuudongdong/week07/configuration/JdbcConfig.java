package com.github.wuudongdong.week07.configuration;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;
import java.util.HashMap;

@Data
@Configuration
@EnableTransactionManagement
public class JdbcConfig implements TransactionManagementConfigurer {
    private static final String MASTER = "master";
    private static final String SLAVE = "slave";

    @Value("${datasource.master.username}")
    String masterUserName;
    @Value("${datasource.master.url}")
    String masterUrl;
    @Value("${datasource.master.password}")
    String masterPassword;

    @Value("${datasource.slave.username}")
    String slaveUserName;
    @Value("${datasource.slave.url}")
    String slaveUrl;
    @Value("${datasource.slave.password}")
    String slavePassword;

    private DataSource masterDataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser(masterUserName);
        dataSource.setPassword(masterUrl);
        dataSource.setURL(masterPassword);
        return dataSource;
    }

    private DataSource slaveDataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser(slaveUserName);
        dataSource.setPassword(slavePassword);
        dataSource.setURL(slaveUrl);
        return dataSource;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        DynamicDataSource dataSource = new DynamicDataSource();
        final DataSource masterDataSource = masterDataSource();
        final DataSource slaveDataSource = slaveDataSource();
        // 初始化值必须设置进去  且给一个默认值
        dataSource.setTargetDataSources(new HashMap<Object, Object>(2, 1) {{
            put(MASTER, masterDataSource);
            put(SLAVE, slaveDataSource);
        }});

        dataSource.setDefaultTargetDataSource(masterDataSource);
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Autowired DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource());
        // 让事务管理器进行只读事务层面上的优化  建议开启
        dataSourceTransactionManager.setEnforceReadOnly(true);
        return dataSourceTransactionManager;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager();
    }
}

