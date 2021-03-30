package com.paramount.bed.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;

import com.paramount.bed.BuildConfig;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.main.RemoteActivity;

public class PermissionUtil {
    public static void showLocationPermissionDialogAlert(Activity activity, PermissionDialogueListener listener) {
        if (!hasLocationPermissions(activity)) {
            DialogUtil.createCustomYesNo(activity, "", LanguageProvider.getLanguage("UI000311C014"),
                    LanguageProvider.getLanguage("UI000311C016"),
                    (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        listener.onPermissionCanceled(dialogInterface);
                    },
                    LanguageProvider.getLanguage("UI000311C015"), (dialogInterface, i) -> {
                        activity.startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)), 200);
                        dialogInterface.dismiss();
                    });
        } else {
            listener.onPermissionGranted();
        }
    }

    public static boolean hasLocationPermissions(Activity activity) {
        return activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static void showLocationServiceDialogAlert(Activity activity, LocationServiceDialogueListener listener) {
        if (!isLocationServiceEnable(activity)) {
            DialogUtil.createCustomYesNo(activity, "", LanguageProvider.getLanguage("UI000311C017"),
                    LanguageProvider.getLanguage("UI000311C019"),
                    (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        listener.onDisabled(dialogInterface);
                    },
                    LanguageProvider.getLanguage("UI000311C018"), (dialogInterface, i) -> {
                        activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 201);
                        dialogInterface.dismiss();
                    });
        } else {
            listener.onEnabled();
        }
    }

    public static boolean isLocationServiceEnable(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static boolean locationFeatureEnabled(Activity activity) {
        return PermissionUtil.hasLocationPermissions(activity) && PermissionUtil.isLocationServiceEnable(activity);
    }

    public interface PermissionDialogueListener {
        void onPermissionCanceled(DialogInterface dialogInterface);

        void onPermissionGranted();
    }

    public interface LocationServiceDialogueListener {
        void onDisabled(DialogInterface dialogInterface);

        void onEnabled();
    }
}
