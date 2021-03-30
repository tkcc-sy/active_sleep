package com.paramount.bed.data.provider;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.paramount.bed.data.model.SliderModel;
import com.paramount.bed.data.remote.response.SliderResponse;
import com.paramount.bed.data.remote.service.HomeService;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SliderProvider {
    public static String DEFAULT_IMAGE_KEY = "default-image-keyword";
    public static String DEFAULT_IMAGE_KEY2 = "default-image-keyword2";
    public static String DEFAULT_IMAGE_KEY3 = "default-image-keyword3";
    public static String DEFAULT_IMAGE_KEY4 = "default-image-keyword4";
    public static String DEFAULT_IMAGE_KEY5 = "default-image-keyword5";
    public static void refreshContent(Context context, HomeService service, ContentProvider.ContentRefreshListener listener){
        service.getSlider(1).enqueue(new Callback<SliderResponse>() {
            @Override
            public void onResponse(Call<SliderResponse> call, Response<SliderResponse> response) {
                SliderResponse sliderResponse = response.body();
                if(sliderResponse != null){
                    if(sliderResponse.isSucces()){
                        SliderModel[] data = sliderResponse.getData();
                        SliderModel.clear();
                        for (SliderModel sliderModel:data
                                ) {
                            Glide.with(context)
                                    .asBitmap()
                                    .load(sliderModel.getImageUrl())
                                    .addListener(new RequestListener<Bitmap>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                            if(listener != null) listener.onContentRefreshFailed("Failed to load image");
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                            return false;
                                        }
                                    })
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            if(resource == null) return ;
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            byte[] imageByte = stream.toByteArray();
                                            resource.recycle();
                                            sliderModel.setImageBytes(imageByte);
                                            sliderModel.insert();
                                            if(SliderModel.getAll().size() >= data.length){
                                                if(listener != null) listener.onContentSliderRefreshSuccess();
                                            }
                                        }
                                    });
                        }
                    }else{
                        if(listener != null)listener.onContentRefreshFailed(sliderResponse.getMessage());
                    }
                }else{
                    if(listener != null)listener.onContentRefreshFailed("Server failure");
                }
            }


            @Override
            public void onFailure(Call<SliderResponse> call, Throwable t) {
                if(listener != null)listener.onContentRefreshNetworkFailure(t);
            }
        });
    }
    public static void init(){
        SliderModel.clear();
        SliderModel defaultSlider1 = new SliderModel();
        defaultSlider1.setId(0);
        defaultSlider1.setCaption(DEFAULT_IMAGE_KEY);
        defaultSlider1.insert();
        SliderModel defaultSlider2 = new SliderModel();
        defaultSlider2.setId(1);
        defaultSlider2.setCaption(DEFAULT_IMAGE_KEY2);
        defaultSlider2.insert();
        SliderModel defaultSlider3 = new SliderModel();
        defaultSlider3.setId(2);
        defaultSlider3.setCaption(DEFAULT_IMAGE_KEY3);
        defaultSlider3.insert();
        SliderModel defaultSlider4 = new SliderModel();
        defaultSlider4.setId(3);
        defaultSlider4.setCaption(DEFAULT_IMAGE_KEY4);
        defaultSlider4.insert();
        SliderModel defaultSlider5 = new SliderModel();
        defaultSlider5.setId(4);
        defaultSlider5.setCaption(DEFAULT_IMAGE_KEY5);
        defaultSlider5.insert();
    }
}
