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
import com.kubixdev.kollect.model.Member;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private final Context context;
    private List<String> memberNames;
    private final FirebaseStorage storage;

    public MemberAdapter(Context context, List<String> memberNames) {
        this.context = context;
        this.memberNames = memberNames;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_item, parent, false);
        return new MemberAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MemberAdapter.ViewHolder holder, int position) {
        String memberName = memberNames.get(position);

        // cancels any previously loaded items to prevent from glitching
        Glide.with(context).clear(holder.imageView);

        // sets member name
        holder.mainText.setText(memberName);

        // retrieves artist details from firestore
        Member.loadMemberData(memberName, new Member.MemberDataLoadListener() {
            @Override
            public void onMemberDataLoaded(Member member) {

                // checks if the adapter is still bound to the correct view
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {

                    // displays images using Glide
                    if ((member != null) && (member.getImage() != null)) {
                        loadImage(holder.imageView, member.getImage());
                    }
                }
            }

            @Override
            public void onMemberDataLoadFailed(String error) {
                Toast.makeText(context.getApplicationContext(), "Failed to load member data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return memberNames.size();
    }


    public void setMemberIds(List<String> memberNames) {
        this.memberNames = memberNames;
        notifyDataSetChanged();
    }


    private void loadImage(ImageView imageView, String fileName) {
        StorageReference gsReference = storage.getReference().child("Members/" + fileName);

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