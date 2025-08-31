package com.example.jpabook.start;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JpaServiceTest {

    @Autowired
    private JpaStartService jpaStartService;

    @Test
    void startTxTest() {
        jpaStartService.startTransaction();
    }
}
