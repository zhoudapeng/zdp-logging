# zdp-logging
作为研发的同学，你是否也遇到过这样的问题？
线上有个订单有问题，去查查日志吧，日志那么多，到底哪些日志是本次请求的啊，根据订单号grep一下吧，
哎，才这么点，而且看不到详细点的日志，异常堆栈也看不到，grep -n试试吧，天啦，那么多无用的日志也显示出来了，
想要的数据还是那么难找，入库的日志在哪里？操作缓存的日志在哪里啊？调用渠道的入参，返回值日志在哪里啊？这次请求的入参，
返回值日志在哪里啊？这次请求总响应时间到底是多少啊？ 
基于这些问题，强烈向大家推荐slf4j的MDC工具，我们可以在日志输出输出时统一输出一个用于标示本次请求的TRACE_ID，
这样当我有了这个TRACE_ID就可以查到这次请求中所有的日志啦,在这个日志中，可以看到请求参数，返回值，耗时，以及每个流程的日志，
包括异步线程调用渠道的日志，那么我们需要做些什么呢？其实很简单  
1.初始化MDC的值，如果系统有使用Web容器，直接在一个Filter里操作，如果是Dubbo接口则在Dubbo的
 Filter里加入如下代码： MDC.put(“TRACE_ID”,UUID.randomUUID().toString());  
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
  另外，如果系统涉及到多个系统直接到调用，我们也完全可以通过把这个TRACE_ID当作请求的一部分发到server端，如果是HttpClient，
  可以通过加个Header的方式，如果是Dubbo的请求可以使用Dubbo提供的RpcContext传递，这样可以在不影响业务参数的同时将一个调用
  中涉及到的多个系统通过一个相同的TRACE_ID串起来，便于分析问题，目前在鉴权服务和短信服务中已经加入此功能，大大提高了定位问题的效率。
  然后在Dubbo中配置此过滤器（provider和consumer都需要配置，配置方式参考 http://www.aichengxu.com/java/2488533.htm）
  这种方式对于单机系统已经够用，但是如果是由多台机器组成的集群，我们就只能一台台去查了，依然很费时间，所以我们后期的规划是在MDC里不仅仅
  加TRACE_ID，我们完全可以在后期把trans_id,mch_trans_id等参数都加入到MDC中，然后接入ELK平台，为不同的字段建索引，最后对外开放一个
  简单易用的Web页面，供开发人员和技术支持的同学使用，不需要登录线上服务器。  
