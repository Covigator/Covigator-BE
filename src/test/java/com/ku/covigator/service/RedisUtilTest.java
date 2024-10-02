package com.ku.covigator.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class RedisUtilTest {

    @Autowired
    private RedisUtil redisUtil;

    @DisplayName("Redis에 데이터를 저장할 수 있다.")
    @Test
    void saveTest() {
        //when
        redisUtil.setDataExpire("key","value", 60);

        //then
        assertThat(redisUtil.existData("key")).isTrue();
    }

    @DisplayName("Redis에 저장된 데이터를 조회할 수 있다.")
    @Test
    void getDataFromRedis() {
        //given
        redisUtil.setDataExpire("key","value", 60);

        //when
        String data = redisUtil.getData("key");

        //then
        assertThat(data).isEqualTo("value");
    }

    @DisplayName("Redis에 저장된 데이터를 삭제할 수 있다.")
    @Test
    void test() {
        //given
        redisUtil.setDataExpire("key","value", 60 * 10);

        //when
        redisUtil.deleteData("key");

        //then
        assertThat(redisUtil.existData("key")).isFalse();
    }

    @DisplayName("Redis에 저장된 데이터는 만료시간이 지나면 삭제된다.")
    @Test
    void expiredTest() {
        //given
        redisUtil.setDataExpire("key", "value", 1L);

        //when //then
        await().pollDelay(Duration.ofSeconds(2)).untilAsserted(
                () -> assertThat(redisUtil.existData("key")).isFalse()
        );
    }

}