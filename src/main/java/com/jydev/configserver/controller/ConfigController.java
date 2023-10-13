package com.jydev.configserver.controller;

import com.jydev.configserver.actuator.ActuatorService;
import com.jydev.configserver.actuator.ActuatorValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ConfigController {

    private final DiscoveryClient discoveryClient;
    private final ActuatorService actuatorService;
    private final ActuatorValidator actuatorValidator;

    @GetMapping("/{profile}")
    public List<String> getUrls(@PathVariable String profile) {
        List<ServiceInstance> instances = discoveryClient.getServices()
                .stream()
                .flatMap(serviceId -> discoveryClient.getInstances(serviceId).stream())
                .toList();

        return actuatorValidator.filter(instances, profile)
                .map(instance -> instance.getUri().toString())
                .toList();
    }

    @PostMapping("/{profile}")
    public List<String> refreshConfig(@PathVariable String profile) {
        List<ServiceInstance> instances = discoveryClient.getServices()
                .stream()
                .flatMap(serviceId -> discoveryClient.getInstances(serviceId).stream())
                .toList();

        List<String> urls = actuatorValidator.filter(instances, profile)
                .map(instance -> instance.getUri().toString())
                .toList();

        actuatorService.call(urls);
        return urls;
    }
}
