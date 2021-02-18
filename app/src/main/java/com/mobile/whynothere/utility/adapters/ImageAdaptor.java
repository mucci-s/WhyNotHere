package com.mobile.whynothere.utility.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.whynothere.R;

import java.util.List;

public class ImageAdaptor extends BaseAdapter {
    List<Bitmap> imageIds;
    Context mContext;
    private LayoutInflater layoutInflater;

    public ImageAdaptor(List<Bitmap> imageIds, Context mContext) {
        this.imageIds = imageIds;
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(android.R.layout.simple_gallery_item, null);
            holder = new ViewHolder();
          //  holder.image = convertView.findViewById(R.id.image_upload);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        holder.image.setLayoutParams(new );
        holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);


        if(imageIds.size() > 0){
            holder.image.setImageBitmap(imageIds.get(position));
        }


        return convertView;
    }

    static class ViewHolder{
        ImageView image;
    }

}
//
//    List<Bitmap> imageIds;
//    Context mContext;
//    private LayoutInflater layoutInflater;
//
//
//    public ImageAdaptor(List<Bitmap> imageIds, Context mContext) {
//        this.imageIds = imageIds;
//        this.mContext = mContext;
//        layoutInflater = LayoutInflater.from(mContext);
//    }
//
//    @Override
//    public int getCount() {
//        return imageIds.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//    public Bitmap getBitmapImage(int pos){
//        return imageIds.get(pos);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        convertView = layoutInflater.inflate(R.layout.image_layout, null);
//
//        ImageView imageView = (ImageView) convertView;
//
//        if(imageView == null){
//            imageView = new ImageView(mContext);
//            imageView.setLayoutParams(new GridView.LayoutParams(400,450));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        }
//
//        imageView.setImageBitmap(imageIds.get(position));
//
//        return imageView;
//    }
//
//    static class ViewHolder{
//        ImageView image;
//        TextView address;
//        TextView coordinates;
//    }
//
//
//}
