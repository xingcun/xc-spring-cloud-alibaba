# ht-project
本案例使用mysql8.0进行处理,引进使用mysql8的json处理方案
引用tkmybatis进行数据库连接，多数据源管理
引用seata1.2进行分布式事务处理
引用nacos进行服务管理



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




# 注意事项
> * 数据库配置文件在xc-service中的application.yml配置文件，引用的是pgsql+hibernate，框架解决了pgsql中的jsonb实例化问题，如需引用其它数据库，请自行更改（最简单的方案就是去除json引用，增加pom数据库引用包）
> * 数据库配置连接上，直接启动就会自动建表
> * 127.0.0.1:8880/api/admin/user/login?username=xx&password=xx 登录,获取token后放在header请求中,xc-token=xxxxxxxxxxxx
> * 如需使用sentinel做监听，可打开xc-service配置文件application.yml中的 sentinel.transport.dashboard,  自行到官网下载,<a href="https://github.com/alibaba/Sentinel/releases" target="_blank">sentinel1.7.0</a>	启动命令 java -Dserver.port=8088 -Dcsp.sentinel.dashboard.server=localhost:8088 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.7.0.jar

# 自动化任务使用说明
* **在xc-service中的resources中,有quartz_innodb.sql(mysql),另一份是有quartz_innodb_pgsql.sql(pgsql)使用的初始化数据库文件**
* **在xc-service中使用job,直接实现接口BaseQuartzJob,并注入到spring bean管理,可使用@Component等方式实现**
* **在xc-admin中使用job,与xc-service相同,然后需有control继承BaseQuartzControl用于远程调度使用,或者可自行实现接收接口**
* **配置参数说明**

| 参数名 | remark |
| ------------ |:-----:|
| name |任务名称|
| cron |cron表达式|
| startDate |运行时间，如果存在，即cron失效|
| isLocalProject |是否xc-service本地运行job|
| url |如果isLocalProject=true,url为空，否则需填入接收任务运行的接口地址,可以直接填xc-admin、或者是http://127.0.0.1/xxxxxxx|
| runJobClass |运行job的class,必须是直接实现接口BaseQuartzJob|
|param|运行时所需要的参数(json)|
|state|0为暂停,1为开始|
|description|任务描述|





在nacos配置上自行增加名为dubbo-sentinel-json的配置即可实现限流
```
[

   {
    "resource": "com.xc.service.user.UserService",
    "count": 1,
    "controlBehavior":2
  }
  
]
```



在nacos配置上自行增加名为gate-sentinel-json的配置即可实现限流
```
[

   {
    "resource": "xc-admin",
    "count": 1,
    "intervalSec":1
  },
   {
    "resource": "aliyun_route",
    "count": 1,
    "intervalSec":1
  }
  
]
```
