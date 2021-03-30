package com.paramount.bed.util;

import com.paramount.bed.data.model.FAQLinkModel;
import com.paramount.bed.data.model.SenderBirdieListModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.FAQLinkResponse;
import com.paramount.bed.data.remote.response.SenderBirdieListResponse;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.data.remote.service.UserService;

import java.util.ArrayList;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class FAQLinkUtil {
    public static void Sync(HomeService service) {
        if (UserLogin.isUserExist()) {
            service.getFAQLink()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<BaseResponse<ArrayList<FAQLinkResponse>>>() {
                        public void onSuccess(BaseResponse<ArrayList<FAQLinkResponse>> response) {
                            if (response != null) {
                                if (response.isSucces()) {
                                    try {
                                        ArrayList<FAQLinkResponse> data = response.getData();
                                        FAQLinkModel.clear();
                                        for (int i = 0; i < data.size(); i++) {
                                            FAQLinkModel faqLinkModel = new FAQLinkModel();
                                            faqLinkModel.setPID(UUID.randomUUID().toString());
                                            faqLinkModel.setLinkNo(data.get(i).getLinkNo());
                                            faqLinkModel.setAppliTag(data.get(i).getAppliTag());
                                            faqLinkModel.insert();
                                        }
                                    } catch (Exception e) {
                                        initialFAQ();
                                    }

                                } else {
                                    initialFAQ();
                                }
                            } else {
                                initialFAQ();
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            initialFAQ();
                        }
                    });
        }
    }

    public static void initialFAQ() {
        if (FAQLinkModel.getAll().size() == 0) {
            ArrayList<FAQLinkModel> data = FAQLinkModel.initialFAQ();
            FAQLinkModel.clear();
            for (int i = 0; i < data.size(); i++) {
                FAQLinkModel faqLinkModel = new FAQLinkModel();
                faqLinkModel.setPID(UUID.randomUUID().toString());
                faqLinkModel.setLinkNo(data.get(i).getLinkNo());
                faqLinkModel.setAppliTag(data.get(i).getAppliTag());
                faqLinkModel.insert();
            }
        }
    }
}
