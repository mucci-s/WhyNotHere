package com.mobile.whynothere.utility.adapters;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.whynothere.NewPlaceActivity;
import com.mobile.whynothere.R;

public class CustomRecyclerAdaptor extends RecyclerView.Adapter<CustomRecyclerAdaptor.MyViewHolder> {

    private Context context;
    private Intent data;
    private int size;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public CustomRecyclerAdaptor(Context context, Intent data) {
        this.context = context;
        this.data = data;
        if (data.getClipData() != null) {
            this.size = data.getClipData().getItemCount();
        } else {
            this.size = 1;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mImage = itemView.findViewById(R.id.image);

            mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (size > 1) {
                        showDialogBox(data.getClipData().getItemAt(getAdapterPosition()).getUri(), context);
                    } else {
                        showDialogBox(data.getData(), context);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_list_photo_newplace, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Uri picUri = null;
        if (data.getClipData() != null) {
            picUri = data.getClipData().getItemAt(position).getUri();
        } else if (data.getData() != null) {
            picUri = data.getData();
        }
        holder.mImage.setImageURI(picUri);

    }


    @Override
    public int getItemCount() {

        return this.size;
    }


    public void showDialogBox(Uri image, Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);

        ImageView Image = dialog.findViewById(R.id.img);
        Image.setImageURI(image);

        dialog.show();
    }


}
