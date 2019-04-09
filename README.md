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
