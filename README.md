# xc-spring-cloud
spring cloud集成框架

| package name | remark |
| ------------ |:-----:|
|xc-service|用于数据库连接，提供service impl类|
|xc-admin|用于提示api连接，提供control类|
|xc-api|用于提供接口信息，和一些共同的实体类|
|xc-common|用于提供一些公共库|


# What does it do
本框架使用spring cloud为基本架构，结合阿里dubbo+nacos提供服务层
再结合hibernate+pgsql+jpa为数据库连接层
以ignite强大的缓存能力为hibernate提供L1,L2缓存


# Start project
因为本项目用到ignite+hibernate，因此个别jar有所改动，已放在xc-service->other中,需自行替换maven库中的jar包
* **先启动nacos，使用的是1.0版的,请自行到nacos官网下载使用**
* **再启动xc-service中的XcServiceBootstrap**
* **再启动xc-admin中的XcAdminBootstrap**
