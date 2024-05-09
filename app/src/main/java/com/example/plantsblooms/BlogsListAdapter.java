// BlogsListAdapter class
package com.example.plantsblooms;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;



public class BlogsListAdapter extends RecyclerView.Adapter<BlogsListAdapter.BlogViewHolder> {

    private ImageView imageView;
    private List<BlogsList> blogsList;
    private Context context;

    private Uri imageUri;

    public BlogsListAdapter(Context context, List<BlogsList> blogsList, Uri imageUri) {
        this.context = context;
        this.blogsList = blogsList;
        this.imageUri = imageUri; // Initialize the image URI
    }


    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blog_list_item, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        BlogsList blog = blogsList.get(position);
        holder.bind(blog, imageUri); // Pass imageUri to bind method
    }


    @Override
    public int getItemCount() {
        return blogsList.size();
    }

    public class BlogViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView userNameTextView;
        private TextView dateTextView;
        private ImageView imageView;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void bind(BlogsList blog, Uri imageUri){
            titleTextView.setText(blog.getTitle());
            descriptionTextView.setText(blog.getDescription());
            userNameTextView.setText(blog.getUserName());

            SharedPreferences sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
            String image_uri = sharedPref.getString("image_uri", "");

            // Set date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
            dateTextView.setText(dateFormat.format(blog.getDate()));

            if (image_uri != null) {
                Glide.with(context)
                        .load(image_uri)
                        .placeholder(R.drawable.ic_search) // Placeholder image while loading
                        .error(R.drawable.camera_alt_24) // Error image if unable to load
                        .into(imageView);
            } else {
                // If no image URI is available, you can set a default image
                imageView.setImageResource(R.drawable.baseline_broken_image_24);
            }
        }
    }
    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
        notifyDataSetChanged(); // Refresh the RecyclerView
    }

}

