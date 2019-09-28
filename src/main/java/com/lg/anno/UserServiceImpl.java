package com.lg.anno;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Xulg
 */
@SuppressWarnings("ConstantConditions")
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private AtomicInteger count = new AtomicInteger(0);

    /**
     * 发生Exception异常进行重试，最大重试次数3次，第一次重试为4秒，第二次为8秒.
     */
    @Retryable(value = BusinessException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    @Override
    public String createUser(String username) {
        count.incrementAndGet();
        if (true) {
            if (count.get() == 1) {
                throw new BusinessException("第一次调用RPC服务失败" + count.get());
            } else {
                throw new BusinessException("调用RPC服务失败" + count.get());
            }
        }
        return username + "$" + count + "$" + UUID.randomUUID().toString();
    }

    /**
     * 重试失败后处理方法
     */
    @Recover
    public String recover(BusinessException e) {
        System.err.println("重试失败后处理方法=====" + "第" + count.get()
                + "次执行RPC调用失败: " + e.getMessage());
        log.info("执行RPC调用失败", e);
        return null;
    }
}