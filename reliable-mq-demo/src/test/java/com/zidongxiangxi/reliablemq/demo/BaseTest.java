package com.zidongxiangxi.reliablemq.demo;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 支持事物，@SpringBootTest 事物默认自动回滚
 *
 * @author chenxudong
 * @since 2019/12/29
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
//@Transactional(rollbackFor = Exception.class)
//@Rollback
public class BaseTest {

    public static final Logger LOG = LoggerFactory.getLogger(BaseTest.class);

}
