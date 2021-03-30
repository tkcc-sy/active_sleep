package com.paramount.bed.util;


import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Objects;

public class SnoreFileUtil {
    public static String SNORE_ANALYSIS_MAIN_FOLDER = "Active Sleep App";
    public static String SNORE_ANALYSIS_TEMP_FOLDER = "snore_temp";
    public static String SNORE_ANALYSIS_RESULT_FOLDER = "snore_highlight";
    public static String SNORE_ANALYSIS_FILE_NAME = "Recording.wav";
    public static String SNORE_ANALYSIS_OUTPUT_FILE_NAME = "AudioAnalysisFile.json";

    public static String getPath(Context ctx){
        return ctx.getFilesDir().getAbsolutePath();
//        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getAnalysisMainFolderPath(Context ctx){
        return getPath(ctx)+"/"+SNORE_ANALYSIS_MAIN_FOLDER;
    }

    public static String getAnalysisTempFolderPath(Context ctx){
        return getAnalysisMainFolderPath(ctx)+"/"+SNORE_ANALYSIS_TEMP_FOLDER;
    }

    public static String getAnalysisResultFolderPath(Context ctx){
        return getAnalysisMainFolderPath(ctx)+"/"+SNORE_ANALYSIS_RESULT_FOLDER;
    }

    public static String getAnalysisAudioInputPath(Context ctx){
        return getAnalysisMainFolderPath(ctx)+"/"+SNORE_ANALYSIS_FILE_NAME;
    }

    public static String getAnalysisAudioInputPathTest(Context ctx){
        String testPath = getAnalysisMainFolderPath(ctx)+"/Test.wav";
        File testFile = new File(testPath);
        return testFile.exists() ? testPath : getAnalysisAudioInputPath(ctx);
    }

    public static String getAnalysisJsonOutputPath(Context ctx){
        return getAnalysisTempFolderPath(ctx)+"/"+SNORE_ANALYSIS_OUTPUT_FILE_NAME;
    }


    public static boolean isSnoreResultExist(String filename, Context ctx){
        String resultPath = getAnalysisResultFolderPath(ctx)+"/"+filename;
        File resultFile = new File(resultPath);
        return resultFile.exists();
    }

    public static String getAnalysisFolderResultContent(Context ctx){
        File parentDir = new File(getAnalysisResultFolderPath(ctx));
        if(parentDir.exists()) {
            ArrayList<String> inFiles = new ArrayList<String>();
            File[] files = parentDir.listFiles();
            if(files != null) {
                for (File file : files) {
                    inFiles.add(file.getAbsolutePath());
                }
                return TextUtils.join(";",inFiles);
            }else{
                return SNORE_ANALYSIS_RESULT_FOLDER+" folder empty";
            }
        }else{
            return SNORE_ANALYSIS_RESULT_FOLDER+" folder not exist";
        }
    }
    public static int getRecordingSize(Context ctx){
        File file = new File(getAnalysisAudioInputPath(ctx));
        return Integer.parseInt(String.valueOf(file.length()/1024));
    }

    public static String getAnalysisResult(Context ctx){
        try {
            StringBuilder data = new StringBuilder();
            FileInputStream fis = new FileInputStream(getAnalysisJsonOutputPath(ctx));
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                data.append(strLine);
            }
            in.close();
            return data.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public static void copy(String origin, String destination) throws IOException {
        File originFile = new File(origin);
        File destinationFile = new File(destination);
        if(!originFile.exists() || destinationFile.exists()){
            throw new IOException();
        }

        FileChannel inChannel = new FileInputStream(originFile).getChannel();
        FileChannel outChannel = new FileOutputStream(destinationFile).getChannel();

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    public static void wipeAnalyzerFiles(Context ctx){
        String extStoragePath = SnoreFileUtil.getAnalysisMainFolderPath(ctx);
        String tempPath = SnoreFileUtil.getAnalysisTempFolderPath(ctx);
        String highlightPath = SnoreFileUtil.getAnalysisResultFolderPath(ctx);

        try {
            //delete temp path
            File tempDir = new File(tempPath);
            if (tempDir.exists() && tempDir.isDirectory() && tempDir.listFiles() != null) {
                //delete all files
                for (File tempDirContent: Objects.requireNonNull(tempDir.listFiles())
                ) {
                    tempDirContent.delete();
                }
                tempDir.delete();
            }

            //delete highlight path
            File higlightDir = new File(highlightPath);
            if (higlightDir.exists() && higlightDir.isDirectory() && higlightDir.listFiles() != null) {
                //delete all files
                for (File higlightDirContent: Objects.requireNonNull(higlightDir.listFiles())
                ) {
                    higlightDirContent.delete();
                }
                higlightDir.delete();
            }

            //delete recording file
            File file = new File(extStoragePath, SnoreFileUtil.SNORE_ANALYSIS_FILE_NAME);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception ignored) {
        }
    }

    public static void wipeTempAnalyzerContent(Context ctx){
        String tempPath = SnoreFileUtil.getAnalysisTempFolderPath(ctx);

        try {
            //delete temp path
            File tempDir = new File(tempPath);
            if (tempDir.exists() && tempDir.isDirectory() && tempDir.listFiles() != null) {
                //delete all files
                for (File tempDirContent: Objects.requireNonNull(tempDir.listFiles())
                ) {
                    tempDirContent.delete();
                }
            }
        } catch (Exception ignored) {
        }
    }
}