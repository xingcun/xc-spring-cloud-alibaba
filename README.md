# spring-cloud-alibaba-by-xc
spring cloud alibaba集成框架可应用于docker部署
已更新集成阿里spring cloud 2.1.1.RELEASE版

ps:已将相对应框架升级为最新版本

演示demo:<a href="https://yangxingcun.com" target="_blank">http://62.234.130.188</a>
sentinel帐号密码为sentinel
nacos 帐号密码为nacos

| package name | remark |
| ------------ |:-----:|
|xc-service|用于数据库连接，提供service impl类|
|xc-admin|用于提示api连接，提供control类|
|xc-api|用于提供接口信息，和一些共同的实体类|
|xc-common|用于提供一些公共库|
|xc-gate|网关,分配路由,用于调用admin类的control层,无需关注端口，路由分配转发|
|xc-ui|后台页面,使用<a href="https://pro.ant.design/index-cn" target="_blank">ant design pro V4</a> react技术开发|

# 项目布署图
![xc-project-deploy](https://user-images.githubusercontent.com/14237778/57747392-02af5700-7708-11e9-8542-9de2fd9a57ce.png)



# What does it do
本框架使用spring cloud为基本架构，结合阿里dubbo+nacos提供服务层
再结合hibernate+pgsql+jpa为数据库连接层
以ignite强大的缓存能力为hibernate提供L1,L2缓存

整合sentinel限流,有sentinel+dubbo+gate
整合bus event与stream，使用robotMq进行通讯
微服务全新架构，整合阿里系的主流架构应用,完全适用于各大主流业务开发  

![](https://user-images.githubusercontent.com/14237778/60095561-673ae880-9781-11e9-9f05-fb7d04d1a75d.png)
![cbc4674336c196da17a8f1716b6d39b](https://user-images.githubusercontent.com/14237778/60095634-9b160e00-9781-11e9-8d83-c928ca4f6a9e.png)
)
![企业微信截图_15625886397651](https://user-images.githubusercontent.com/14237778/60810018-b0962980-a1be-11e9-93f1-d788ea6bfd04.png)
![56a150f482970b8dc1ab8625ac5c79c](https://user-images.githubusercontent.com/14237778/60096038-930a9e00-9782-11e9-8a60-957a607c6871.png)



# Start project

* **先启动nacos，使用的是1.0版的,请自行到<a href="https://github.com/alibaba/nacos/releases" target="_blank">nacos官网下载</a>使用**
* **再启动xc-service中的XcServiceBootstrap**
* **再启动xc-admin中的XcAdminBootstrap**
* **再启动xc-gate中的GateBootstrap**
* **配置nginx,配置文件参考<a href="https://github.com/xingcun/xc-spring-cloud-alibaba/tree/master/xc-ui/docker/nginx.conf" target="_blank">nginx.conf</a>,启动前端文件**
* **增加Mysql8.0并支持json操作,修改xc-service中的application.yml数据库配置，再修改BaseEntity，User类**

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
