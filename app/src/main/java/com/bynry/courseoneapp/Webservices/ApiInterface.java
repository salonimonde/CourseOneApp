package com.bynry.courseoneapp.Webservices;

import com.bynry.courseoneapp.Models.Collection;
import com.bynry.courseoneapp.Models.Photo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("photos")
    Call<List<Photo>> getPhotos();

    @GET("collections/featured")
    Call<List<Collection>> getCollections();
}
