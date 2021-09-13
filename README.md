模拟spring的一个myspring框架，实现了IoC，AOP，MVC功能 

整体框架基于JDK反射实现，并使用cglib动态代理实现AOP

TODO1:设计BeanDefinition保存Bean信息 

TODO2:设计类似spring的三级缓存实现使用时加载Bean，并解决循环依赖问题（目前版本不会有循环依赖问题，因为所有Bean都在最开始一起加载完再统一注入） 

TODO3:设计类似spring的BeanPostProcessor，并使用BeanPostProcessor重构AOP部分 

这几个是大的改动计划，还有一些小的，做的时候再写
