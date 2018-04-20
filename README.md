# zdp-logging
作为研发的同学，你是否也遇到过这样的问题？
线上有个订单有问题，去查查日志吧，日志那么多，到底哪些日志是本次请求的啊，根据订单号grep一下吧，
哎，才这么点，而且看不到详细点的日志，异常堆栈也看不到，grep -n试试吧，天啦，那么多无用的日志也显示出来了，
想要的数据还是那么难找，入库的日志在哪里？操作缓存的日志在哪里啊？调用渠道的入参，返回值日志在哪里啊？这次请求的入参，
返回值日志在哪里啊？这次请求总响应时间到底是多少啊？ 
基于这些问题，强烈向大家推荐slf4j的MDC工具，我们可以在日志输出输出时统一输出一个用于标示本次请求的TRACE_ID，
这样当我有了这个TRACE_ID就可以查到这次请求中所有的日志啦,在这个日志中，可以看到请求参数，返回值，耗时，以及每个流程的日志，
包括异步线程调用渠道的日志，那么我们需要做些什么呢？其实很简单  
1.初始化MDC的值,目前提供2种Filter，分别是基于Servlet容器的和Dubbo的
如果系统对外提供的是Http的接口，且基于Servlet容器，则只需在web.xml中这样配置:
   ```java
        <filter>
            <filter-name>mdcFilter</filter-name>
            <filter-class>com.zdp.logging.filter.ServletLogFilter</filter-class>
        </filter>
        <filter-mapping>
            <filter-name>mdcFilter</filter-name>
            <url-pattern>/*</url-pattern>
        </filter-mapping>
   ```
如果系统对外提供的是Dubbo接口，则只需这样配置
在resources目录下添加纯文本文件META-INF/dubbo/com.alibaba.dubbo.rpc.Filter，内容如下
```java
mdcFilter=com.zdp.logging.filter.DubboLogFilter
```
修改dubbo的provider或者consumer配置文件，在dubbo:provider中添加配置的filter，如下：
```java
<dubbo:provider filter="mdcFilter" />
<dubbo:reference filter="mdcFilter" />
```
2.在logback配置文件中Appender的layout添加  [TRACE_ID:%X{TRACE_ID}]   
3.由于MDC的内部实现是InheritableThreadLocal,当我们直接new一个Thread的子类或者Runnable的实现类并直接调用其start()方法时，
  MDC里的值是能成功继承给子线程的，但是如果任务是提交给线程池执行的话，就需要我们做一些额外的工作，这里我提供了一个工具类和一个包装类，
  直接这样使用就OK  
  ```java
  executor.execute(new RunnableWrapper(new Runnable(){
      public void run() {
        // TODO 
      }
  }));
  ```
  另外，如果涉及到多个系统之间的调用，我们需要将这个TRACE_ID在系统之间传递，如果是Http请求，
  需要放到Header（避免影响到业务参数）发到server端，如果是Dubbo的请求可以使用Dubbo提供的RpcContext传递，这样可以在不影响业务参数的同时将一个
  相同的TRACE_ID串起来，便于分析问题，大大提高了定位问题的效率。
  这种方式对于单机系统已经够用，但是如果是由多台机器组成的集群，我们就只能一台台去查了，依然很费时间，所以我们后期的规划是在MDC里不仅仅
  加TRACE_ID，我们完全可以在后期把用户id，订单号等参数都加入到MDC中，然后接入ELK平台，为不同的字段建索引，最后对外开放一个
  简单易用的Web页面，供开发人员和技术支持的同学使用，不需要登录线上服务器。  
