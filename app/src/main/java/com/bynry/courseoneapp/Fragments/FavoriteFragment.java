package com.bynry.courseoneapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bynry.courseoneapp.Adapters.PhotosAdapter;
import com.bynry.courseoneapp.Models.Photo;
import com.bynry.courseoneapp.R;
import com.bynry.courseoneapp.Utils.RealmController;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FavoriteFragment extends Fragment {

    @BindView(R.id.fragment_favorite_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.fragment_favorite_notification)
    TextView notification;

    private PhotosAdapter photosAdapter;
    private List<Photo> photos = new ArrayList<>();
    private Unbinder unbinder;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        unbinder = ButterKnife.bind(this, view);

        //Recycler View

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        photosAdapter = new PhotosAdapter(getActivity(), photos);
        recyclerView.setAdapter(photosAdapter);

        getPhotos();

        return view;
    }

    private void getPhotos(){
        RealmController realmController = new RealmController();
        photos.addAll(realmController.getPhotos());
        if (photos.size() == 0){
            notification.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            photosAdapter.notifyDataSetChanged();
            notification.setVisibility(View.GONE);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
