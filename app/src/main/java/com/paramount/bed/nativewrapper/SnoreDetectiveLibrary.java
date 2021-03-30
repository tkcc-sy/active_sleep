package com.paramount.bed.nativewrapper;

public class SnoreDetectiveLibrary{
    //NATIVE WRAPPER
    public native String stringFromJNI();
    public native long SDL_SnoreInitialize();
    public native long SDL_GetErrorCode();
    public native long SDL_SnoreAnalysis(String filePath,String outPath,int snoTime,int snoreTh,int outInterval,int outSnoreFileTime,int outSnoreFileCont);
    public native long SDL_SnoreAnalysisCancel();
}