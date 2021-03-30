package com.paramount.bed.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.FileProvider;
import android.view.KeyEvent;
import android.view.View;

import com.paramount.bed.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.List;

public class ActivityUtil {
    public static boolean isForeground(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }

        return false;
    }

    public static View handleBackButton(View view){
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                }
                return false;
            }
        });

        return view;
    }

    public static void navigationFragment(FragmentActivity act, int id, Fragment fragment){
        FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.snoreFragment, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public static void navigationFragmentAllowStateLoss(FragmentActivity act, int id, Fragment fragment){
        FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.snoreFragment, fragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    public static File storeScreenshot(Bitmap bitmap) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String filename = "img_paramount_" + timestamp.toString() + ".jpeg";
        String pathDir = Environment.getExternalStorageDirectory().toString() + "/Paramount";

        File dir = new File(pathDir);
        if (!dir.exists()){
            dir.mkdir();
        }

        OutputStream out = null;
        File imageFile = new File(pathDir + "/" + filename);

        System.out.println(imageFile.getAbsolutePath());

        try {
            out = new FileOutputStream(imageFile);
            // choose JPEG format
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
        } catch (FileNotFoundException e) {
            // manage exception ...
        } catch (IOException e) {
            // manage exception ...
        } finally {

            try {
                if (out != null) {
                    out.close();
                }

            } catch (Exception exc) {
            }

        }
        return imageFile;
    }
    public static Bitmap takescreenshot(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return b;
    }

    public static Bitmap takescreenshotOfRootView(View v) {
        return takescreenshot(v.getRootView());
    }

    public static void shareActionView(Activity context, File file){
        Uri photoURI;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            photoURI = Uri.fromFile(file);
        }else{
            photoURI = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "share via"));
    }
}
