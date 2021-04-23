package com.paramount.bed.data.provider;

import android.content.Context;
import androidx.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.paramount.bed.data.model.DeviceTemplateBedModel;
import com.paramount.bed.data.model.DeviceTemplateMattressModel;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.DeviceTemplateResponse;
import com.paramount.bed.data.remote.service.HomeService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceTemplateProvider {
    public static void getDeviceTemplate(Context ctx, DeviceTemplateFetchListener listener, int userId, Integer bed_type) {
        HomeService homeService = ApiClient.getClient(ctx).create(HomeService.class);
        Call<BaseResponse<DeviceTemplateResponse>> method;
        if (bed_type == null) {
            method = homeService.getDeviceTemplate(userId,1);
        } else {
            method = homeService.getDeviceTemplate(userId,1, bed_type);
        }

//        method = homeService.getDeviceTemplate(userId,1, 3);  // 「INTIME COMFORT」用設定値を取得するデバッグ処理

        method.enqueue(new Callback<BaseResponse<DeviceTemplateResponse>>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse<DeviceTemplateResponse>> call, @NonNull Response<BaseResponse<DeviceTemplateResponse>> response) {
                if(response.body() != null && response.body().getData() != null){

                    DeviceTemplateBedModel.clear();
                    DeviceTemplateMattressModel.clear();

                    DeviceTemplateResponse data = response.body().getData();
                    for (DeviceTemplateBedModel bedModel:data.bedDefault
                            ) {
                        bedModel.setDefault(true);
                        bedModel.insert();
                    }
                    for (DeviceTemplateMattressModel mattressModel:data.mattressDefault
                            ) {
                        mattressModel.setDefault(true);
                        mattressModel.insert();
                    }
                    for (DeviceTemplateBedModel bedModel:data.bed
                            ) {
                        bedModel.insert();
                    }
                    for (DeviceTemplateMattressModel mattressModel:data.mattress
                            ) {
                        mattressModel.insert();
                    }
                    NemuriConstantsModel.clear();
                    data.constants.insert();

//                    Logger.d("設定値 ベッドタイプ:%d", bed_type);
//                    Logger.d("設定値 一時停止高さ:%d", data.constants.heightWarningThreshold);
//                    Logger.d("設定値 高さデフォルト値:%d", data.bed.get(0).getHeight_default());
//                    Logger.d("設定値 傾斜デフォルト値:%d", data.bed.get(0).getTilt_default());

                    if(listener != null){
                        listener.onDeviceTemplateFetched(data.getMattress(),data.getBed(),data.getMattressDefault(),data.getBedDefault(),NemuriConstantsModel.get());
                    }
                }else{
                    //load local data
                    if(listener != null){
                        listener.onDeviceTemplateFetched(DeviceTemplateProvider.getOrCreateMattressTemplate(false),DeviceTemplateProvider.getOrCreateBedTemplate(false),
                                                        DeviceTemplateProvider.getOrCreateMattressTemplate(true),DeviceTemplateProvider.getOrCreateBedTemplate(true),
                                NemuriConstantsModel.get());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<DeviceTemplateResponse>> call, @NonNull Throwable t) {
                //load local data
                if(listener != null){
                    listener.onDeviceTemplateFetched(DeviceTemplateProvider.getOrCreateMattressTemplate(false),DeviceTemplateProvider.getOrCreateBedTemplate(false),
                            DeviceTemplateProvider.getOrCreateMattressTemplate(true),DeviceTemplateProvider.getOrCreateBedTemplate(true),
                            NemuriConstantsModel.get());
                }
            }
        });
    }

    private static List<DeviceTemplateMattressModel> getOrCreateMattressTemplate(boolean isDefault)
    {
        List<DeviceTemplateMattressModel> mattressModels = DeviceTemplateMattressModel.getAll(isDefault);
        if(mattressModels.isEmpty()){
            mattressModels = DeviceTemplateMattressModel.getOriginalValues();
        }
        return mattressModels;
    }
    private static List<DeviceTemplateBedModel> getOrCreateBedTemplate(boolean isDefault)
    {
        List<DeviceTemplateBedModel> bedModels = DeviceTemplateBedModel.getAll(isDefault);
        if(bedModels.isEmpty()){
            bedModels = DeviceTemplateBedModel.getOriginalValues();
        }
        return bedModels;
    }
    public interface DeviceTemplateFetchListener{
       void onDeviceTemplateFetched(List<DeviceTemplateMattressModel> mattressModels, List<DeviceTemplateBedModel> bedModels,
                                        List<DeviceTemplateMattressModel> mattressModelDefaults, List<DeviceTemplateBedModel> bedModelDefaults,NemuriConstantsModel nemuriConstantsModel);
    }
}
