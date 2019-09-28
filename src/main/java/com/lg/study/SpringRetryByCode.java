package com.lg.study;

import com.lg.anno.BusinessException;
import com.lg.anno.UserService;
import com.lg.anno.UserServiceImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.retry.*;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.DefaultRetryState;
import org.springframework.retry.support.RetryTemplate;

import java.util.Collections;

/**
 * 使用编程式的SpringRetry功能
 *
 * @author Xulg
 * Created in 2019-09-28 13:51
 */
public class SpringRetryByCode {
    private static final Logger logger = LoggerFactory.getLogger(SpringRetryByCode.class);

    /*
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
     *
     * 熔断模式
     *      @CircuitBreaker
     */

    /**
     * 无状态的重试
     */
    @Test
    public void testWithNoState() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // 执行业务逻辑
        UserService userService = new UserServiceImpl();
        RetryCallback<String, BusinessException> business = new RetryCallback<String, BusinessException>() {
            @Override
            public String doWithRetry(RetryContext context) throws BusinessException {
                System.err.println(context.toString());
                return userService.createUser("张三");
            }
        };

        // 设置重试策略
        RetryPolicy retryPolicy = new SimpleRetryPolicy(3,
                Collections.singletonMap(BusinessException.class, true));
        retryTemplate.setRetryPolicy(retryPolicy);

        // 设置回避策略(两次重试之间的回避策略),默认1000ms
        BackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        retryTemplate.setBackOffPolicy(backOffPolicy);

        // 设置补偿策略
        RecoveryCallback<String> recovery = new RecoveryCallback<String>() {
            @Override
            public String recover(RetryContext context) {
                System.err.println("重试失败后处理方法=====" + "第" + context.getRetryCount()
                        + "次执行RPC调用失败: " + context.getLastThrowable().getMessage());
                return null;
            }
        };

        // 执行
        String result = retryTemplate.execute(business, recovery);
        System.err.println(result);
    }

    /**
     * 有状态的重试
     */
    @Test
    public void testWithState() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // 执行业务逻辑
        UserService userService = new UserServiceImpl();
        RetryCallback<String, BusinessException> business = new RetryCallback<String, BusinessException>() {
            @Override
            public String doWithRetry(RetryContext context) throws BusinessException {
                System.err.println(context.toString());
                return userService.createUser("张三");
            }
        };

        // 设置有状态
        RetryState retryState = new DefaultRetryState("MyKey", false,
                new BinaryExceptionClassifier(Collections.singletonMap(BusinessException.class, true)));

        // 设置重试策略
        RetryPolicy retryPolicy = new SimpleRetryPolicy(3,
                Collections.singletonMap(BusinessException.class, true));
        retryTemplate.setRetryPolicy(retryPolicy);

        // 设置回避策略(两次重试之间的回避策略),默认1000ms
        BackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        retryTemplate.setBackOffPolicy(backOffPolicy);

        // 设置补偿策略
        RecoveryCallback<String> recovery = new RecoveryCallback<String>() {
            @Override
            public String recover(RetryContext context) {
                System.err.println("重试失败后处理方法=====" + "第" + context.getRetryCount()
                        + "次执行RPC调用失败: " + context.getLastThrowable().getMessage());
                return null;
            }
        };

        // 执行
        try {
            String result = retryTemplate.execute(business, recovery, retryState);
            System.err.println(result);
        } catch (Exception e) {
            System.err.println("异常了啊: " + e);
        }
    }
}
