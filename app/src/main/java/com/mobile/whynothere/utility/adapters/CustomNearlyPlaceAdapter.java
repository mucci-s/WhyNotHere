package com.mobile.whynothere.utility.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobile.whynothere.R;
import com.mobile.whynothere.models.PlaceDataInfoModel;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomNearlyPlaceAdapter extends BaseAdapter {

    private List<PlaceDataInfoModel> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomNearlyPlaceAdapter(Context acontext, List<PlaceDataInfoModel> listData) {
        this.listData = listData;
        this.context = acontext;
        layoutInflater = LayoutInflater.from(acontext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public PlaceDataInfoModel getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getPlaceSelectedID(int position) {
        return listData.get(position).getPlaceID();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {

        ViewHolder holder;

        if (view == null) {

            view = layoutInflater.inflate(R.layout.place_info_layout, null);
            holder = new ViewHolder();
            holder.title = view.findViewById(R.id.titleNearID);
            holder.address = view.findViewById(R.id.addressNearID);
            holder.coordinates = view.findViewById(R.id.coordinatesNearID);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        Geocoder geocoder = new Geocoder(context);

        List<Address> locationList = new ArrayList<>();

        try {
            locationList = geocoder.getFromLocation(listData.get(position).getPosition().latitude, listData.get(position).getPosition().longitude, 1);
        } catch (IOException e) {
            Log.e("TAG", "geolocate: IOException: " + e.getMessage());
        }

        if (locationList.size() > 0) {

            holder.title.setText(listData.get(position).getTitle());
            holder.address.setText(locationList.get(0).getAddressLine(0));
            holder.coordinates.setText(listData.get(position).getPosition().toString().substring(8));
            Log.i("posto", locationList.get(0).toString());

        }

        return view;

    }

    static class ViewHolder {
        TextView title;
        TextView address;
        TextView coordinates;
    }

}
