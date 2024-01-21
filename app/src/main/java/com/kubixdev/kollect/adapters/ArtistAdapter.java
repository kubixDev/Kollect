package com.kubixdev.kollect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.model.Artist;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    private final Context context;
    private List<String> artistNames;
    private final FirebaseStorage storage;
    private ArtistClickListener artistClickListener;

    public ArtistAdapter(Context context, List<String> artistNames, ArtistClickListener listener) {
        this.context = context;
        this.artistNames = artistNames;
        this.storage = FirebaseStorage.getInstance();
        this.artistClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artist_item, parent, false);
        return new ViewHolder(view);
    }

    public interface ArtistClickListener {
        void onArtistClicked(String artistName);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String artistName = artistNames.get(position);

        // cancels any previously loaded artists to prevent from glitching
        Glide.with(context).clear(holder.imageView);

        // sets artist name
        holder.mainText.setText(artistName);

        // retrieves artist details from firestore
        Artist.loadArtistData(artistName, new Artist.ArtistDataLoadListener() {
            @Override
            public void onArtistDataLoaded(Artist artist) {

                // checks if the adapter is still bound to the correct view
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {

                    // displays images using Glide
                    if ((artist != null) && (artist.getImage() != null)) {
                        loadImage(holder.imageView, artist.getImage());
                    }
                }
            }

            @Override
            public void onArtistDataLoadFailed(String error) {
                Toast.makeText(context.getApplicationContext(), "Failed to load artist data", Toast.LENGTH_SHORT).show();
            }
        });


        holder.itemView.setOnClickListener(v -> {
            if (artistClickListener != null) {
                artistClickListener.onArtistClicked(artistName);
            }
        });
    }


    @Override
    public int getItemCount() {
        return artistNames.size();
    }


    public void setArtistIds(List<String> artistNames) {
        this.artistNames = artistNames;
        notifyDataSetChanged();
    }


    private void loadImage(ImageView imageView, String fileName) {
        StorageReference gsReference = storage.getReference().child("Artists/" + fileName);

        try {
            // creates a temporary file to store the downloaded image
            File localFile = File.createTempFile("images", "jpg");

            // downloads the image
            gsReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {

                // loads the downloaded image
                Glide.with(context)
                        .load(localFile)
                        .placeholder(R.drawable.placeholderimage)
                        .into(imageView);

            }).addOnFailureListener(exception -> {
                Toast.makeText(context.getApplicationContext(), "Failed to download image", Toast.LENGTH_SHORT).show();
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView mainText;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            mainText = view.findViewById(R.id.mainText);
        }
    }
}