package com.example.plantsblooms;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {

    private List<Plant> plants;
    private Context context;

    public PlantAdapter(List<Plant> plants, Context context) {
        this.plants = plants;
        this.context = context;
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_card, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant currentPlant = plants.get(position);

        // Load the image into the ImageView using a library like Picasso or Glide
        String imageUrl = currentPlant.getImageUrl();
        Picasso.get().load(imageUrl).fit().centerInside().into(holder.imageViewPlant);

        // Bind other data to TextViews
        holder.textViewTitle.setText(currentPlant.getTitle());
        holder.textViewType.setText(currentPlant.getType());
        holder.textViewDescription.setText(currentPlant.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to open DiscoverActivity
                Intent intent = new Intent(context, DiscoverActivity.class);
                // Pass the selected plant object as extra
                intent.putExtra("plant_object", currentPlant);
                // Start the DiscoverActivity
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPlant;
        TextView textViewTitle;
        TextView textViewType;
        TextView textViewDescription;

        PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPlant = itemView.findViewById(R.id.imageViewPlant);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewType = itemView.findViewById(R.id.textViewType);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}
