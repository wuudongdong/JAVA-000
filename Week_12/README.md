## 学习笔记
----
- 作业一 (必做) 配置 redis 的主从复制，sentinel 高可用，Cluster 集群

Redis主从复制配置注意事项：
1. 从库配置主库的地址 replicaof 172.17.0.2 6379
2. 修改启动的端口 port 6380
3. 注释掉绑定的ip # bind 127.0.0.1
4. 关闭保护模式 protected-mode no
5. 设置密码 requirepass 123456

sentinel高可用配置注意事项：
