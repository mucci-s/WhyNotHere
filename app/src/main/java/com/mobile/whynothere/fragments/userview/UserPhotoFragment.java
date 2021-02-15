package com.mobile.whynothere.fragments.userview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mobile.whynothere.R;
import com.mobile.whynothere.utility.adapters.DefaultImageAdaptor;

import java.util.ArrayList;
import java.util.Arrays;

public class UserPhotoFragment extends Fragment {

    private GridView gridView;

    ArrayList<Integer> defaultImages = new ArrayList<>(Arrays.asList(
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.avatar_icon,
            R.drawable.heart_grey,
            R.drawable.map_blue,
            R.drawable.avatar_icon,
            R.drawable.default_icon_place,
            R.drawable.avatar_icon,
            R.drawable.heart_grey,
            R.drawable.map_blue,
            R.drawable.avatar_icon,
            R.drawable.default_icon_place
    ));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_photo, container, false);

        this.gridView = root.findViewById(R.id.gridViewID);
        this.setDefaultImages(gridView);

        return root;
    }

    public void setDefaultImages(GridView gridView) {
        DefaultImageAdaptor defaultImageAdaptor = new DefaultImageAdaptor(this.defaultImages, getContext());
        gridView.setAdapter(defaultImageAdaptor);
        gridView.setEnabled(false);
    }

}
