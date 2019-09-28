package com.lg.study;

/**
 * Spring Retry框架学习
 *
 * @author Xulg
 * Created in 2019-09-28 10:06
 */
@SuppressWarnings("all")
public class SpringRetryDemo {

    /*
     * 概念
     *  spring对于重试机制的实现，给了几个抽象。
     *      BackOff：补偿值，一般指失败后多久进行重试的延迟值。
     *      Sleeper：暂停应用的工具，通常用来应用补偿值。
     *      BackOffPolicy：补偿策略，决定失败后如何确定补偿值。
     *      RetryContext：重试上下文，代表了能被重试动作使用的资源。
     *      RetryPolicy：重试策略，决定失败能否重试。
     *      RecoveryCallback：定义一个动作recover，在重试耗尽后的动作。
     *      RetryCallback：具体的重试动作。
     *      RetryOperations：通过传递RetryCallback，进行重试操作。
     *      RetryState：重试状态，通常包含一个重试的键值。
     *      RetryStatistics和RetryListener，用来监控Retry的执行情况，并生成统计信息。
     */

    /*
     *注解方式使用
     *
     * @EnableRetry
     *      启用Retry功能
     *
     * @Retryable
     *      标注此注解的方法在发生异常时会进行重试
     *          需要被重试的方法加上@Retryable(),就能在指定的异常出现情况下重试,而当默认的失败次数到达后,
     *          就会调用@Recover注解的方法，进行恢复。
     *          属性说明：
     *              value: 指定重试的异常类,excludes为空的话,处理所有异常
     *              include: 和value一样
     *              exclude: 排除重试的异常类
     *              maxAttempts: 最大重试次数(包含第一次执行失败的),默认是3次
     *                          例如maxAttempts=3，则第一次执行失败后，会尝试2次。
     *              backoff: 重试补偿策略,使用@Backoff注解
     *              stateful: 设置是否有状态
     *                        有状态：异常将会被重新抛出
     *                        无状态：异常不会被重新抛出
     *
     * @Backoff
     *      重试补偿策略,设置失败后多久进行重试的延迟值
     *          不设置参数时，默认使用FixedBackOffPolicy(指定等待时间)，重试等待1000ms
     *          只设置delay,使用FixedBackOffPolicy(指定等待- - 重试等待固定的delay时间)
     *          设置delay和maxDelay,使用FixedBackOffPolicy(指定等待- - 设置delay和maxDealy时，重试等待在这两个值之间均态分布)
     *          设置delay、maxDealy、multiplier，使用ExponentialBackOffPolicy(指数级重试间隔的实现)，multiplier即指定延迟倍数，
     *              比如delay=5000L,multiplier=2,则第一次重试为5秒，第二次为10秒，第三次为20秒.
     *
     * @Recover
     *      用于@Retryable重试失败后处理方法,可以在该方法中进行日志处理.
     *      注意：
     *          1.此注解注释的方法参数一定要是@Retryable抛出的异常,否则无法识别,
     *          2.此注解注释的方法返回值必须和@Retryable注释的方法的返回值一致，否则无法识别
     *
     */

    /*
     *编码方式使用
     *
     * 重试策略：
     *      1.NeverRetryPolicy：只调用RetryCallback一次，不重试
     *      2.AlwaysRetryPolicy：无限重试，最好不要用
     *      3.SimpleRetryPolicy：重试n次，默认3，也是模板默认的策略。很常用
     *      4.TimeoutRetryPolicy：在n毫秒内不断进行重试，超过这个时间后停止重试
     *      5.CircuitBreakerRetryPolicy：熔断功能的重试，关于熔断，请参考：使用hystrix保护你的应用
     *      6.ExceptionClassifierRetryPolicy：可以根据不同的异常，执行不同的重试策略，很常用
     *      7.CompositeRetryPolicy：将不同的策略组合起来，有悲观组合和乐观组合。
     *                              悲观默认重试，有不重试的策略则不重试。
     *                              乐观默认不重试，有需要重试的策略则重试。
     *
     * 支持的回避策略如下：
     *      1.NoBackOffPolicy：不回避
     *      2.FixedBackOffPolicy：n毫秒退避后再进行重试
     *      3.UniformRandomBackOffPolicy：随机选择一个[n,m]（如20ms，40ms)回避时间回避后，然后在重试
     *      4.ExponentialBackOffPolicy：指数退避策略，休眠时间指数递增
     *      5.ExponentialRandomBackOffPolicy：随机指数退避，指数中乘积会混入随机值
     *          注意：
     *              (1)如何执行回避?
     *                  一般使用ThreadWaitSleeper，即当前线程直接sleep一段时间。
     *              (2)凡是带有随机性的策略，大多都是为了避免惊群效应，防止相同时间执行大量操作。
     *
     * 监听器和监控
     *      监听器接口org.springframework.retry.RetryListener的API如下：
     *          // 第一次重试的时候会执行该方法
     *          <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback);
     *
     *          // 重试结束后会调用改方法
     *          <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable);
     *
     *          // 每次重试产生异常时会调用改方法
     *          <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable);
     *
     * 有状态和无状态的重试
     *      无状态重试，这意味着产生异常，并不会将其抛出去，对于事务性调用，这是不可容忍的，
     *      因为上层框架需要获得异常进行事务的回滚操作。此时应当使用有状态重试
     *
     * 熔断模式
     *      @CircuitBreaker
     */
}