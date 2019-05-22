# xc-spring-cloud-alibaba
spring cloud alibaba集成框架可应用于docker部署

| package name | remark |
| ------------ |:-----:|
|xc-service|用于数据库连接，提供service impl类|
|xc-admin|用于提示api连接，提供control类|
|xc-api|用于提供接口信息，和一些共同的实体类|
|xc-common|用于提供一些公共库|
|xc-gate|网关,分配路由,用于调用admin类的control层,无需关注端口，路由分配转发|

# 项目布署图
![xc-project-deploy](https://user-images.githubusercontent.com/14237778/57747392-02af5700-7708-11e9-8542-9de2fd9a57ce.png)



# What does it do
本框架使用spring cloud为基本架构，结合阿里dubbo+nacos提供服务层
再结合hibernate+pgsql+jpa为数据库连接层
以ignite强大的缓存能力为hibernate提供L1,L2缓存

# version log
2019-04-15 
> *  此版本增加了xc-gate网关功能，下个版本会增加权限检验等功能
> * 更换nacos-client为1.0.0-RC4主要目的是解决多dubbo客户端注册的问题,[nacos服务端引用正式版1.0](https://github.com/alibaba/nacos/releases)	
> *  增加baseControl接口，基本的增删改查，实现该接口即可完成
> *  增加公共的ignite缓存层,通过CacheFactoryv即可获取.

2019-04-16
> *  此版本增加了sentinel+dubbo+nacos 服务限流

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

2019-04-17
> *  此版本修改了sentinel+dubbo+nacos 服务限流的方式，更改为spring cloud的引用具体配置在xc-service中的application.yml文件


2019-04-22
> *  此版本增加了ExecutorThreadConfig 内部线程执行器,可通过xc.executor.enabled进行启用与配置
> *  增加了BaseSchedule,简易的分布式任务执行功能
> *  增加分布式锁的使用与监听

2019-04-24
> *  更改dubbo服务连接方式,将alibaba dubbo更换与appache dubbo2.7.1,使用spring-cloud连接方式进行服务注册


2019-04-25
> *  此版本增加了gate+sentinel+nacos 服务限流


2019-05-09
> *  此版本主要修改配置文件适配docker swarm部署项目



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



# Start project

* **先启动nacos，使用的是1.0版的,请自行到nacos官网下载使用**
* **再启动xc-service中的XcServiceBootstrap**
* **再启动xc-admin中的XcAdminBootstrap**
* **再启动xc-gate中的GateBootstrap**

# 注意事项
> * 数据库配置文件在xc-service中的application.yml配置文件，引用的是pgsql+hibernate，框架解决了pgsql中的jsonb实例化问题，如需引用其它数据库，请自行更改（最简单的方案就是去除json引用，增加pom数据库引用包）
> * 数据库配置连接上，直接启动就会自动建表
> * 访问127.0.0.1:8880/api/admin/user/regist 注册用户
```
{
    "mobile":13800138000,
    "password":123456
}
```

> * 127.0.0.1:8880/api/admin/user/login?username=xx&password=xx 登录,获取token后放在header请求中,xc-token=xxxxxxxxxxxx
> * 如需使用sentinel做监听，可打开xc-service配置文件application.yml中的 sentinel.transport.dashboard,  自行到官网下载,[sentinel1.5.1](https://github.com/alibaba/Sentinel/releases)	启动命令 java -Dserver.port=8088 -Dcsp.sentinel.dashboard.server=localhost:8088 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.5.1.jar
 