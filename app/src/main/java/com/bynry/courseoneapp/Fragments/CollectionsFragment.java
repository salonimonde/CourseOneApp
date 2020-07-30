package com.bynry.courseoneapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bynry.courseoneapp.Adapters.CollectionsAdapter;
import com.bynry.courseoneapp.Models.Collection;
import com.bynry.courseoneapp.R;
import com.bynry.courseoneapp.Utils.Functions;
import com.bynry.courseoneapp.Webservices.ApiInterface;
import com.bynry.courseoneapp.Webservices.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionsFragment extends Fragment {
    private final String TAG = CollectionsFragment.class.getSimpleName();

    @BindView(R.id.collections_grid_view)
    GridView gridView;

    @BindView(R.id.collections_progress_bar)

    ProgressBar progressBar;

    private Unbinder unbinder;
    private CollectionsAdapter collectionsAdapter;
    private List<Collection> collections = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fargment_collections, container, false);
        unbinder = ButterKnife.bind(this, view);
        collectionsAdapter = new CollectionsAdapter(getActivity(), collections);
        gridView.setAdapter(collectionsAdapter);

        showProgressBar(true);
        getCollections();
        return view;
    }

    @OnItemClick(R.id.collections_grid_view)
    public void setGridView(int position){
        Collection collection = collections.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("collectionId", collection.getId());

        CollectionFragment collectionFragment = new CollectionFragment();
        collectionFragment.setArguments(bundle);
        Functions.changeMainFragmentWithBack(getActivity(),collectionFragment);
    }

    private void getCollections() {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<List<Collection>> call = apiInterface.getCollections();
        call.enqueue(new Callback<List<Collection>>() {
            @Override
            public void onResponse(Call<List<Collection>> call, Response<List<Collection>> response) {
                if (response.isSuccessful()){
                    collections.addAll(response.body());
                    collectionsAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Fail" + response.message());
                }
                showProgressBar(false);
            }

            @Override
            public void onFailure(Call<List<Collection>> call, Throwable t) {
                Log.e(TAG, "Fail" + t.getMessage());
                showProgressBar(false);
            }
        });
    }

    private void showProgressBar(boolean isShowing) {
        if (isShowing) {
            progressBar.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
