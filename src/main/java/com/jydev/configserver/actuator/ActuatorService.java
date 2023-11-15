package com.jydev.configserver.actuator;

import com.jydev.configserver.config.ActuatorFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActuatorService {

    private final ActuatorFeignClient feignClient;

    public void call(List<String> urls) {
        urls.forEach(url -> {
            try {
                feignClient.refresh(URI.create(url));
                log.info("Success for URL: {}", url);
            } catch (Exception e) {
                log.warn("Error for URL {}: {}", url, e.getMessage());
                // TODO: 3번 리트라이 실패 시 슬랙 알림
            }
        });
    }
}
