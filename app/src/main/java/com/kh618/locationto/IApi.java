package com.kh618.locationto;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IApi {

    String baseURL = "https://maps.googleapis.com/maps/api/directions/";

    @GET
    Call<JSONObject> getDirection(@Url String url);
}
