package com.jydev.configserver.config;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.jydev")
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignClientErrorDecoder();
    }

    /**
     * period: 하나의 요청 완료와 다음 재시도 시작 간의 기간
     * maxPeriod: 재시도 간의 최대 기간
     * maxAttempts: 실패한 요청을 재시도할 최대 횟수
     */
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(1000, 2000, 3);
    }
}
