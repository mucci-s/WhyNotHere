package com.mobile.whynothere.fragments.userview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.mobile.whynothere.R;
import com.mobile.whynothere.utility.adapters.ImageAdaptor;

public class UserLikedPhotoFragment extends Fragment {

    private ImageView comingSoon;

    int[] defaultImages = {
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
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.avatar_icon,
            R.drawable.heart_grey,
            R.drawable.map_blue,
            R.drawable.avatar_icon,
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
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.avatar_icon,
            R.drawable.heart_grey,
            R.drawable.map_blue,
            R.drawable.avatar_icon
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_userliked_photo, container, false);

       this.comingSoon = root.findViewById(R.id.comingSoonImage);
       this.comingSoon.setImageDrawable(getResources().getDrawable(R.drawable.coming_soon));
       this.comingSoon.setVisibility(View.VISIBLE);
        return root;
    }

//    public void setDefaultImages() {
//        ImageAdaptor defaultImageAdaptor = new ImageAdaptor(pics,getContext());
//        this.gridView.setAdapter(defaultImageAdaptor);
//
//    }

}
