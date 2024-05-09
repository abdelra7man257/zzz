package com.example.plantsblooms

data class PlantModel(
    val title: String,
    val type: String,
    val image: Int,
    val arObject: String
            ) {
    companion object {
        fun plantsList(): List<PlantModel> {
            return listOf(
                PlantModel(
                    "Snake Plant",
                    "Outdoor"
                    , R.drawable.snake_plant_outdoor,
                    "https://firebasestorage.googleapis.com/v0/b/chatty-app-96e4a.appspot.com/o/snake_plant_in_pot.glb?alt=media&token=c602c4e9-34b0-4310-b690-d65d4a5f8a49"
                ),
                PlantModel(
                    "Rigged Plant",
                    "Indoor"
                    , R.drawable.rigged_indoor_plant,
                    "https://firebasestorage.googleapis.com/v0/b/chatty-app-96e4a.appspot.com/o/rigged_indoor-plant_animation_test.glb?alt=media&token=bc5a72fa-05e3-41ce-8a0c-7942126bdf57"
                )
            )
        }
    }
}
