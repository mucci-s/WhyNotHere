package com.mobile.whynothere.utility.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

public class DefaultImageAdaptorComment extends BaseAdapter {

    List<Integer> imageIds;
    Context mContext;

    public DefaultImageAdaptorComment(List<Integer> defaultImage, Context mContext) {
        this.imageIds = defaultImage;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return imageIds.size();
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

        ImageView imageView = (ImageView) convertView;

        if(imageView == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(80,70));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        imageView.setImageResource(imageIds.get(position));


        return imageView;
    }
}
