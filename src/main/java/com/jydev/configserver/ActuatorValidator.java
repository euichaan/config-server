package com.jydev.configserver;

import com.jydev.configserver.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
public class ActuatorValidator {

    private static final String META_DATA_PROFILE_KEY = "profile";
    private static final String META_DATA_DEFAULT_KEY = "default";
    private static final String META_DATA_PROFILE_ALLOW_ALL = "all";

    @Value("${spring.application.name}")
    private String data;

    public Stream<ServiceInstance> filter(List<ServiceInstance> services, String profile) {
        return services.stream()
                .filter(instance -> isNotConfigServer(instance.getServiceId()))
                .filter(instance -> isAllowAllProfile(profile) || extractActiveProfiles(instance).contains(profile));
    }

    private boolean isNotConfigServer(String serviceId) {
        return !data.toUpperCase().equals(serviceId);
    }

    private boolean isAllowAllProfile(String profile) {
        return META_DATA_PROFILE_ALLOW_ALL.equals(profile);
    }

    private List<String> extractActiveProfiles(ServiceInstance instance) {
        String profilesString = instance.getMetadata()
                .getOrDefault(META_DATA_PROFILE_KEY, META_DATA_DEFAULT_KEY);

        return Arrays.stream(StringUtil.removeAllWhiteSpace(profilesString)
                .split(",")).toList();
    }
}
