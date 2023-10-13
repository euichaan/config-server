package com.jydev.configserver.actuator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActuatorService {

    private static final String ACTUATOR_REFRESH = "/actuator/refresh";

    private final WebClient webClient;

    public void call(List<String> urls) {
        Flux.fromIterable(urls)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(url -> webClient.post()
                        .uri(url + ACTUATOR_REFRESH)
                        .retrieve()
                        .bodyToMono(String.class)
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)) // 최대 3번 리트라이, 1초 간격
                                .maxBackoff(Duration.ofSeconds(5))) // 최대 5초 간격
                        .doOnSuccess(response -> log.info("Success for URL: {}", url))
                        .doOnError(error -> log.warn("Error for URL: {}: {}", url, error.getMessage()))) // TODO 3번 리트라이 실패 시 슬랙 알림
                .subscribe();
    }
}
