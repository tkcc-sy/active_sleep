package com.paramount.bed.util;

import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.SenderBirdieListModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.NemuriConstantsResponse;
import com.paramount.bed.data.remote.response.PasswordPolicyResponse;
import com.paramount.bed.data.remote.response.SenderBirdieListResponse;
import com.paramount.bed.data.remote.service.NemuriScanService;
import com.paramount.bed.data.remote.service.UserService;

import java.util.ArrayList;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SenderBirdieListUtil {
    public static void Sync(UserService service) {
        if (UserLogin.isUserExist()) {
            service.getBirdieNotifList(UserLogin.getUserLogin().getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<BaseResponse<ArrayList<SenderBirdieListResponse>>>() {
                        public void onSuccess(BaseResponse<ArrayList<SenderBirdieListResponse>> response) {
                            if (response != null) {
                                if (response.isSucces()) {
                                    try {
                                        ArrayList<SenderBirdieListResponse> data = response.getData();
                                        for (int i = 0; i < data.size(); i++) {
                                            SenderBirdieListModel senderBirdieListModel = new SenderBirdieListModel();
                                            senderBirdieListModel.setPID(UUID.randomUUID().toString());
                                            senderBirdieListModel.setMonitoredUserId(data.get(i).getMonitoredUserId());
                                            senderBirdieListModel.setMonitoredNickname(data.get(i).getMonitoredNickname());
                                            senderBirdieListModel.setCreatedDate(data.get(i).getCreatedDate());
                                            senderBirdieListModel.insert();
                                        }
                                    } catch (Exception e) {

                                    }

                                } else {
                                }
                            } else {
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                        }
                    });
        }
    }
}
