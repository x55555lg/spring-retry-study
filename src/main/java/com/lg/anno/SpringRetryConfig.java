package com.lg.anno;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * SpringRetry的配置
 *
 * @author Xulg
 * Created in 2019-09-28 10:52
 */
@Configuration
@EnableRetry// 开启spring的重试功能
public class SpringRetryConfig {
}
