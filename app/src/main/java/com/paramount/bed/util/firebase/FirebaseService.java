package com.paramount.bed.util.firebase;

import android.app.PendingIntent;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.paramount.bed.BedApplication;
import com.paramount.bed.data.model.SenderBirdieModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.main.HomeActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.paramount.bed.data.model.UserLogin.isUserExist;

public class FirebaseService extends FirebaseMessagingService {
    private UserService userService = ApiClient.getClient(BedApplication.getsApplication()).create(UserService.class);
    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(FirebaseService.this);
    }

    @Override
    public void onNewToken(String token) {
        if (isUserExist()) {
            userService.fcmUpdateToServer(UserLogin.getUserLogin().getId(), token, 1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<BaseResponse<String>>() {
                        @Override
                        public void onSuccess(BaseResponse<String> stringBaseResponse) {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }
                    });
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (isUserExist()) {
            showNotification(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(), remoteMessage.getData().get("sender"), remoteMessage.getNotification().getClickAction());
        }

    }

    private void showNotification(String title, String body, String sender, String clickAction) {
        Intent intent = new Intent("onNotif", null, this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("sender", sender);
        intent.putExtra("click_action", clickAction);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        broadcaster.sendBroadcast(intent);
        Boolean isBirdie = clickAction != null && clickAction.equals("BIRDIE_BUTTON") ? true : false;
        if (isBirdie && sender != null && !sender.isEmpty()) {
            SenderBirdieModel.updateByName(sender);
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
