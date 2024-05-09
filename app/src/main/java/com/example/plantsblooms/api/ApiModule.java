package com.example.plantsblooms.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiModule {

    //design pattern -> singleton

    private static Retrofit retrofit;

    private static Retrofit getApiInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl("https://96d2-197-63-175-62.ngrok-free.app/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
   public static WebServices getWebServices(){

        return getApiInstance().create(WebServices.class);
      //          baseurl                 endpoint

    }
}
