**学习笔记**
--------
#### 作业一 （必做）读写分离 动态切换数据源版本 1.0
思路：
使用spring的AbstractRoutingDataSource来实现，其中用到了ThreadLocal和aop

核心逻辑如下：

1. 继承spring的AbstractRoutingDataSource实现获取dataSourceId的逻辑
```java
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
```

2. 向spring容器中注册动态数据源
```java
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
```

3. 使用自定义注解切换数据源
```java
import com.github.wuudongdong.week07.annotation.ReadOnly;
import com.github.wuudongdong.week07.configuration.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Order(1)
@Aspect
@Component
public class DynamicDataSourceHandlerAspect {

    @Pointcut("@annotation(com.github.wuudongdong.week07.annotation.ReadOnly)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void doBefore(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        ReadOnly annotationClass = method.getAnnotation(ReadOnly.class);
        if (annotationClass == null) {
            annotationClass = joinPoint.getTarget().getClass().getAnnotation(ReadOnly.class);
            if (annotationClass == null){
                return;
            }
        }

        // 获取注解上的数据源的值的信息（这里最好还判断一下dataSourceId是否是有效的  若是无效的就用warn提醒  此处我就不处理了）
        String dataSourceId = annotationClass.dataSourceId();

        // 此处切换数据源
        DynamicDataSource.LOOKUP_KEY_HOLDER.set(dataSourceId);
        log.info("AOP动态切换数据源，className" + joinPoint.getTarget().getClass().getName() + "methodName" + method.getName() + ";dataSourceId:" + dataSourceId == "" ? "默认数据源" : dataSourceId);
    }


    @After("pointcut()")
    public void after(JoinPoint point) {
    }
}
```