package com.lg.study;

import com.lg.anno.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * 使用基于注解版的SpringRetry功能
 *
 * @author Xulg
 * Created in 2019-09-28 10:29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringRetryByAnnotation {

    @Resource
    private UserService userService;

    @Test
    public void test() throws Exception {
        String user = userService.createUser("张三");
        System.err.println(user);
        Thread.sleep(100000);
    }

}