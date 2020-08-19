package com.devtahreem.testproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testproject.R;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.testproject.R.drawable.dp1;

public class UserCardAdapter extends RecyclerView.Adapter<UserCardAdapter.ExampleViewHolder> {
    private ArrayList<UserCardDetails> mUserCardDetails;


    public static class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;
        public TextView textViewName, userIdInvisible;
        public TextView textViewAddress, textViewAge, textViewOccupation;


        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewAge = itemView.findViewById(R.id.textViewAge);
            textViewOccupation = itemView.findViewById(R.id.textViewOccupation);
            userIdInvisible = itemView.findViewById(R.id.userId);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }



    UserCardAdapter(ArrayList<UserCardDetails> exampleList) {
        mUserCardDetails = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card_adapter, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        final UserCardDetails currentItem = mUserCardDetails.get(position);

        holder.userIdInvisible.setText(currentItem.getCurrentUserId());
        holder.mImageView.setImageBitmap(currentItem.getUserImage());
        holder.textViewName.setText(currentItem.getName());
        holder.textViewAddress.setText(currentItem.getAddress());
        holder.textViewOccupation.setText(currentItem.getOccupation());
        holder.textViewAge.setText(currentItem.getAge());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), UserAccountConnect.class);
                intent.putExtra("User id", currentItem.getCurrentUserId());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserCardDetails.size();
    }


}
