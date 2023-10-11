package com.jydev.configserver;


import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ActuatorAPI {

    @POST("/actuator/refresh")
    Call<Void> refresh(@Url String url);
}
