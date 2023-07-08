package com.example.imagepro;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GreetingsFragment extends Fragment implements PhotoAdapter.OnPhotoClickListener {

    private  List<Integer> photoList;
    public GreetingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_greetings, container, false);
        RecyclerView photoRecyclerView = rootView.findViewById(R.id.photoRecyclerView);

        photoList = new ArrayList<>(createPhotoList());
        PhotoAdapter photoAdapter = new PhotoAdapter(requireContext(), photoList);
        photoAdapter.setOnPhotoClickListener(this);
        photoRecyclerView.setAdapter(photoAdapter);
        return rootView;
    }
    private List<Integer> createPhotoList() {
        TypedArray photoArray = getResources().obtainTypedArray(R.array.greetings_array);
        List<Integer> photoList = new ArrayList<>();
        for (int i = 0; i < photoArray.length(); i++) {
            int resourceId = photoArray.getResourceId(i, 0);
            photoList.add(resourceId);
        }
        photoArray.recycle();
        return photoList;
    }
    @Override
    public void onPhotoClick(int position) {
        int clickedPhotoResourceId = photoList.get(position);

        Intent intent = new Intent(requireContext(), FullScreenImage.class);
        intent.putExtra(FullScreenImage.EXTRA_PHOTO_RES_ID, clickedPhotoResourceId);
        startActivity(intent);
    }
}