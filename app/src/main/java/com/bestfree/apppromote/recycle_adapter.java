package com.bestfree.apppromote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.storage.FirebaseStorage;

public class recycle_adapter extends RecyclerView.Adapter{
    private List<each_app_class> meach_app = new ArrayList<>();
    private Context mcontext;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference = storage.getReference();
    private FirebaseUser user = auth.getCurrentUser();
    each_app_class items;
    View v;
    public static class ViewHolder extends RecyclerView.ViewHolder{


        TextView app_name;
        ImageView app_icon;
        RelativeLayout each_app_layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            each_app_layout = itemView.findViewById(R.id.each_app_layout);
            app_name = itemView.findViewById(R.id.app_name);
            app_icon = itemView.findViewById(R.id.app_icon);
        }
    }




    public recycle_adapter(List<each_app_class> each_app ,Context context)
    {
        this.meach_app = each_app;
        this.mcontext = context;
    }

    @Override
    public int getItemViewType(int position) {


        return 0;

    }

/*
    public recycle_adapter(@NonNull FirestoreRecyclerOptions<comment_items> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull comment_items model) {
        holder.comment_user_content.setText(model.getuser_content());
        Picasso.get().load(model.getImage()).into(holder.comment_user_image);
        holder.comment_user_name.setText(model.getuser_name());
    }*/


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.each_app, viewGroup, false);
        return new ViewHolder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        items = (each_app_class) meach_app.get(i);


        if(i == 0) {
            viewHolder.app_name.setText(items.getApp_name());

            StorageReference this_user_pic = storageReference.child(items.getPackage_string());
            this_user_pic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(mcontext).load(uri.toString()).dontAnimate().into(viewHolder.app_icon);
                }
            });
        }else{
            meach_app.remove(i);
            //removeData(i);
            viewHolder.each_app_layout.setVisibility(View.GONE);

        }


    }



    @Override
    public int getItemCount() {
        return meach_app.size();
    }


}

