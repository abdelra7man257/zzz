package com.example.plantsblooms.api;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WebServices {

    /*
        https://96d2-197-63-175-62.ngrok-free.app/predict
        api
        baseurl/endpoint
        main thread-> User interface ->block main thread -> freeze
        background
        @body
        multipart form


    */

    @POST("predict")
    Call<PlantPredictionResponse> getPrediction(@Body RequestBody image);
}
