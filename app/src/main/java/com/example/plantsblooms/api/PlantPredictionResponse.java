package com.example.plantsblooms.api;

import com.google.gson.annotations.SerializedName;

public class PlantPredictionResponse{

	@SerializedName("full-prediction")
	public FullPrediction fullPrediction;

	@SerializedName("more")
	 public String more;

	@SerializedName("prediction")
	 public String prediction;
}