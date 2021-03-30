package com.paramount.bed.data.provider;

import android.content.Context;
import android.util.Log;

import com.paramount.bed.data.model.AppStateModel;
import com.paramount.bed.data.model.LanguageModel;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguageProvider {
    public static String[] languageString = {"en-US", "jp-JP", "id-ID"};

    public static void refreshContent(HomeService service, ContentProvider.ContentRefreshListener listener, String languageCode) {
        service.getLanguage(languageCode,1).enqueue(new Callback<BaseResponse<LanguageModel[]>>() {
            @Override
            public void onResponse(Call<BaseResponse<LanguageModel[]>> call, Response<BaseResponse<LanguageModel[]>> response) {
                BaseResponse<LanguageModel[]> languageModelBaseResponse = response.body();
                if (languageModelBaseResponse != null) {
                    if (languageModelBaseResponse.isSucces()) {
                        LanguageModel[] data = languageModelBaseResponse.getData();
                        ArrayList<LanguageModel> arr = new ArrayList<>();

                        LanguageModel.clear(languageCode);

                        for (LanguageModel languageModel : data
                                ) {
                            languageModel.setLanguageCode(languageCode);
                            arr.add(languageModel);
                        }
                        LanguageModel.batchInsert(arr);
                        listener.onContentLanguageRefreshSuccess();
                    } else {
                        if (listener != null)
                            listener.onContentRefreshFailed(languageModelBaseResponse.getMessage());
                    }
                } else {
                    if (listener != null) listener.onContentRefreshFailed("Server failure");
                }
            }


            @Override
            public void onFailure(Call<BaseResponse<LanguageModel[]>> call, Throwable t) {
                if (listener != null) listener.onContentRefreshNetworkFailure(t);
            }
        });
    }

    public static String getLanguage(String tag) {
        LanguageModel data = LanguageModel.getByTag(tag, AppStateModel.getLocale());
        if (data == null) return tag;
        return data.getContent();
    }

    public static void init(Context context) {
        LanguageModel.clear();
        //load lang from local json
        for (String languageCode : languageString
                ) {
            try {
                String jsonString = JsonUtil.loadJSONFromAsset(context, languageCode + ".json");
                if (jsonString != null) {
                    JSONObject obj = new JSONObject(jsonString);
                    JSONArray dataArray = obj.getJSONArray("language");
                    ArrayList<LanguageModel> arr = new ArrayList<>();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject singleLang = dataArray.getJSONObject(i);
                        String tag = singleLang.getString("tag");
                        String content = "";
                        try {
                            content = singleLang.getString("content");
                        } catch (Exception e) {
                            content = ("JSON" + tag);
                        }
                        LanguageModel languageModel = new LanguageModel();
                        languageModel.setContent(content);
                        languageModel.setTag(tag);
                        languageModel.setLanguageCode(languageCode);

                        arr.add(languageModel);
                    }
                    LanguageModel.batchInsert(arr);
                    Log.d("LOCALIZATION", "Local language loaded with " + arr.size() + " data");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
