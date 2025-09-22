package com.qd33.bilibili_analysis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.qd33.bilibili_analysis.repository.UserRepository;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        // 简单的上下文加载测试
        assertThat(userRepository).isNotNull();
    }

    @Test
    void testUserRepository() {
        long count = userRepository.count();
        System.out.println("用户表记录数: " + count);
        assertThat(count).isGreaterThanOrEqualTo(0);
    }
}