# ht-project
本案例使用mysql8.0进行处理,引进使用mysql8的json处理方案
引用tkmybatis进行数据库连接，多数据源管理
引用seata1.2进行分布式事务处理
引用nacos进行服务管理
引用shardingSphere进行分库分表处理


# Start project

* **先启动nacos，使用的是1.2版的,请自行到<a href="https://github.com/alibaba/nacos/releases" target="_blank">nacos官网下载</a>使用**
* **下载<a href="https://github.com/seata/seata/releases" target="_blank">seata1.2</a>**
* **修改seata配置文件中的file.conf 更改store.mode=db 再修改其数据库连接**
```
#mysql8连接
dbType = "mysql"
driverClassName = "com.mysql.cj.jdbc.Driver"
url = "jdbc:mysql://172.31.1.111:3306/seata?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=GMT%2B8"
```
* **建立seata数据库，请使用ht-server下的seata.sql文件**
* **再修改registry.conf文件，更改registry.type=nacos，再视情况修改nacos连接**
```
registry {
  # file 、nacos 、eureka、redis、zk、consul、etcd3、sofa
  type = "nacos"

  nacos {
    application = "seata-server"
    serverAddr = "127.0.0.1:8848"
    namespace = ""
    cluster = "default"
    username = ""
    password = ""
  }
 }
 config {
   # file、nacos 、apollo、zk、consul、etcd3
   type = "file"
  }
```
* **再启动seata,windows运行seata-server.bat,mac运行seata-server.sh**
* **修改ht-server项目中registry.conf配置文件,主要配置nacos地址**
* **修改ht-server项目中file.conf配置文件,主要配置service.default.grouplist地址,为seata服务启动地址,端口默认8091**
* **建立两个数据库分别导入dbxc.sql**
* **修改application.yml的数据库连接**
* **启动项目**



