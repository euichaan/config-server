package com.jydev.configserver;

import com.jydev.configserver.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ConfigController {

    private static final String META_DATA_PROFILE_KEY = "profile";
    private static final String META_DATA_DEFAULT_KEY = "default";
    private static final String META_DATA_PROFILE_ALLOW_ALL = "all";

    @Value("${spring.application.name}")
    private String data;

    private final DiscoveryClient discoveryClient;
    private final ActuatorService actuatorService;

    @GetMapping("/{profile}")
    public List<String> getUrls(@PathVariable String profile) {
        return discoveryClient.getServices()
                .stream()
                .flatMap(serviceId -> discoveryClient.getInstances(serviceId).stream())
                .filter(instance -> !isConfigServer(instance.getServiceId()) && isAllowAllProfile(profile) || extractActiveProfiles(instance).contains(profile))
                .map(instance -> instance.getUri().toString())
                .collect(Collectors.toList());
    }

    @PostMapping("/{profile}")
    public void refreshConfig(@PathVariable String profile) {
        List<String> urls = discoveryClient.getServices()
                .stream()
                .flatMap(serviceId -> discoveryClient.getInstances(serviceId).stream())
                .filter(instance -> !isConfigServer(instance.getServiceId()) && isAllowAllProfile(profile) || extractActiveProfiles(instance).contains(profile))
                .map(instance -> instance.getUri().toString())
                .collect(Collectors.toList());

        actuatorService.call(urls);
    }

    private boolean isConfigServer(String serviceId){
        return data.toUpperCase().equals(serviceId);
    }

    private boolean isAllowAllProfile(String profile){
        return META_DATA_PROFILE_ALLOW_ALL.equals(profile);
    }

    private List<String> extractActiveProfiles(ServiceInstance instance) {
        String profilesString = instance.getMetadata()
                .getOrDefault(META_DATA_PROFILE_KEY, META_DATA_DEFAULT_KEY);

        return Arrays.stream(StringUtil.removeAllWhiteSpace(profilesString)
                        .split(",")).toList();
    }
}
