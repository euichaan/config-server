package com.jydev.configserver.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URI;

@FeignClient(url = "http://test", name = "test") // url 명시적으로 제공 필요
public interface ActuatorFeignClient {

    @PostMapping(value = "/actuator/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    void refresh(URI baseUrl);
}
