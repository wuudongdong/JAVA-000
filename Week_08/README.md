**学习笔记**
----------
#### 作业一 （必做）设计对前面的订单表数据进行水平分库分表，拆分 2 个库，每个库 16 张表。并在新结构在演示常见的增删改查操作。代码、sql 和配置文件，上传到 Github

思路：
使用shardingsphere-proxy对订单表做分库分表

1. 极简订单表建表语句

```sql
CREATE TABLE order_master (order_id bigint, customer_id bigint) ENGINE = innodb;
```

2. 修改proxy的server.yaml配置文件

```yaml
authentication:
  users:
    root:
      password: root
    sharding:
      password: sharding
      authorizedSchemas: sharding_db
 
 props:
  max-connections-size-per-query: 1
  acceptor-size: 16  # The default value is available processors count * 2.
  executor-size: 16  # Infinite by default.
  proxy-frontend-flush-threshold: 128  # The default value is 128.
    # LOCAL: Proxy will run with LOCAL transaction.
    # XA: Proxy will run with XA transaction.
    # BASE: Proxy will run with B.A.S.E transaction.
  proxy-transaction-type: LOCAL
  proxy-opentracing-enabled: false
  proxy-hint-enabled: false
  query-with-cipher-column: true
  sql-show: false
  check-table-metadata-enabled: false
```
3. 修改proxy的config-sharding.yaml配置文件

```yaml
schemaName: sharding_db

dataSources:
 ds_0:
   url: jdbc:mysql://172.17.0.1:3306/demo_ds_0?serverTimezone=UTC&useSSL=false
   username: root
   password: 123456
   connectionTimeoutMilliseconds: 30000
   idleTimeoutMilliseconds: 60000
   maxLifetimeMilliseconds: 1800000
   maxPoolSize: 50
   minPoolSize: 1
   maintenanceIntervalMilliseconds: 30000
 ds_1:
   url: jdbc:mysql://172.17.0.1:3306/demo_ds_1?serverTimezone=UTC&useSSL=false
   username: root
   password: 123456
   connectionTimeoutMilliseconds: 30000
   idleTimeoutMilliseconds: 60000
   maxLifetimeMilliseconds: 1800000
   maxPoolSize: 50
   minPoolSize: 1
   maintenanceIntervalMilliseconds: 30000

rules:
- !SHARDING
 tables:
   order_master:
     actualDataNodes: ds_${0..1}.order_master_${0..15}
     tableStrategy:
       standard:
         shardingColumn: order_id
         shardingAlgorithmName: t_order_inline
     keyGenerateStrategy:
       column: order_id
       keyGeneratorName: snowflake
 bindingTables:
   - order_master
 defaultDatabaseStrategy:
   standard:
     shardingColumn: customer_id
     shardingAlgorithmName: database_inline
 defaultTableStrategy:
   none:
 
 shardingAlgorithms:
   database_inline:
     type: INLINE
     props:
       algorithm-expression: ds_${customer_id % 2}
   t_order_inline:
     type: INLINE
     props:
       algorithm-expression: order_master_${order_id % 16}
 
 keyGenerators:
   snowflake:
     type: SNOWFLAKE
     props:
       worker-id: 123
```
3. 拉取apache/sharding-proxy镜像

`docker pull apache/sharding-proxy`

4. 启动sharding-proxy容器

`docker run -d -v /${your_work_dir}/shardingsphere-proxy/conf:/opt/sharding-proxy/conf -v /${your_work_dir}/shardingsphere-proxy/ext-lib:/opt/sharding-proxy/ext-lib -e JVM_OPTS="-Djava.awt.headless=true" -e PORT=3308 -p13308:3308 apache/sharding-proxy`

notice：<br>

需要将mysql的驱动配置到proxy的扩展包ext-lib下

5. 使用mysql连接proxy建表


