## 学习笔记
----
- 作业一 (必做) 配置 redis 的主从复制，sentinel 高可用，Cluster 集群

Redis主从复制时从库配置注意事项：
1. 从库配置主库的地址 replicaof 172.17.0.2 6379
2. 修改启动的端口 port 6380
3. 注释掉绑定的ip # bind 127.0.0.1
4. 关闭保护模式 protected-mode no

sentinel高可用配置注意事项：
1. 修改sentinel的启动端口
2. 修改sentinel监控的master的ip和端口及判断客观下线的sentinel的个数 sentinel monitor mymaster 172.17.0.3 6380 2
3. 修改sentinel的id sentinel myid 0a6dfebf4369c7c94fe0a8a16795d4ba84e764f5

Redis Cluster配置注意事项：

