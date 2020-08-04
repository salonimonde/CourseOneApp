package com.bynry.courseoneapp.Acivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bynry.courseoneapp.Models.Photo;
import com.bynry.courseoneapp.R;
import com.bynry.courseoneapp.Utils.Functions;
import com.bynry.courseoneapp.Utils.GlideApp;
import com.bynry.courseoneapp.Utils.RealmController;
import com.bynry.courseoneapp.Webservices.ApiInterface;
import com.bynry.courseoneapp.Webservices.ServiceGenerator;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullScreenPhotoActivity extends AppCompatActivity {

    @BindView(R.id.activity_fullscreen_photo_photo)
    ImageView fullScreenPhoto;
    @BindView(R.id.activity_fullscreen_photo_user_avatar)
    CircleImageView userAvatar;
    @BindView(R.id.activity_fullscreen_photo_fab_menu)
    FloatingActionMenu fabMenu;
    @BindView(R.id.activity_fullscreen_photo_fab_favorite)
    FloatingActionButton fabFavorite;
    @BindView(R.id.activity_fullscreen_photo_fab_wallpaper)
    FloatingActionButton fabWallpaper;

    @BindView(R.id.activity_fullscreen_photo_username)
    TextView username;

    private Bitmap photoBitmap;

    private Unbinder unbinder;

    private RealmController realmController;

    @BindDrawable(R.drawable.ic_check_favorite)
    Drawable icFavorite;
    @BindDrawable(R.drawable.ic_check_favorited)
    Drawable icFavorited;
    private Photo photo;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_photo);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        unbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        String photoId = intent.getStringExtra("photoId");
        getPhoto(photoId);

        realmController = new RealmController();
        if (realmController.isPhotoExist(photoId)){
            fabFavorite.setImageDrawable(icFavorited);
        }
    }

    private void getPhoto(String id) {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<Photo> call = apiInterface.getPhoto(id);
        call.enqueue(new Callback<Photo>() {
            @Override
            public void onResponse(Call<Photo> call, Response<Photo> response) {
                if (response.isSuccessful()) {
                    photo = response.body();
                    updateUI(photo);
                } else {

                }
            }

            @Override
            public void onFailure(Call<Photo> call, Throwable t) {

            }
        });
    }


    private void updateUI(Photo photo) {
        try {
            username.setText(photo.getUser().getUserName());
            GlideApp.with(FullScreenPhotoActivity.this)
                    .load(photo.getUser().getProfileImage().getSmall())
                    .into(userAvatar);

            GlideApp.with(FullScreenPhotoActivity.this)
                    .asBitmap()
                    .load(photo.getUrl().getRegular())
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            fullScreenPhoto.setImageBitmap(resource);
                            photoBitmap = resource;
                        }
                    });
        } catch (Exception e) {

        }
    }

    @OnClick(R.id.activity_fullscreen_photo_fab_favorite)
    public void setFabFavorite() {
        if (realmController.isPhotoExist(photo.getId())){
            realmController.deletePhoto(photo);
            fabFavorite.setImageDrawable(icFavorite);
            Toast.makeText(FullScreenPhotoActivity.this, "Remove Favorite", Toast.LENGTH_SHORT).show();
        } else {
            realmController.savePhoto(photo);
            fabFavorite.setImageDrawable(icFavorited);
            Toast.makeText(FullScreenPhotoActivity.this, "Favorited", Toast.LENGTH_SHORT).show();
        }
        fabMenu.close(true);
    }

    @OnClick(R.id.activity_fullscreen_photo_fab_wallpaper)
    public void setFabWallpaper() {
        if (photoBitmap != null ){
            if (Functions.setWallPaper(FullScreenPhotoActivity.this, photoBitmap)){
                Toast.makeText(FullScreenPhotoActivity.this, "Wallpaper Set Successfully!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FullScreenPhotoActivity.this, "Set Wallpaper Failed!!", Toast.LENGTH_SHORT).show();
            }
        }
        fabMenu.close(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
