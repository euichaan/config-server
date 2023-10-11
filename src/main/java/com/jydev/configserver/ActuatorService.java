package com.jydev.configserver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
public class ActuatorService {

    private final ActuatorAPI actuatorAPI;

    public void call(List<String> urls) {
        BlockingQueue<HttpRetryInfo> queue = new LinkedBlockingQueue<>();
        urls.stream()
                .map(HttpRetryInfo::fromUrl)
                .forEach(queue::offer);
        while (!queue.isEmpty()) {
            HttpRetryInfo retryInfo = queue.poll();
            actuatorAPI.refresh(retryInfo.url()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        }
    }
}
