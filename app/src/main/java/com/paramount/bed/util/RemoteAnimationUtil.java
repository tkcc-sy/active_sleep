package com.paramount.bed.util;

public class RemoteAnimationUtil {
    public static int getAdjustedHead(int value){
        return (value * 75)/100;
    }
    public static float getDeltaY(float target) {
        target = Math.abs(target);
        float result = 0;
        for (float c = 0; c < target; c = c + 3f) {
            result = result + 0.2f;
        }
        return result;
    }

    public static float getDeltaX(float target) {
        target = Math.abs(target);
        float result = 0;
        for (float c = 0; c < target; c = c + 3f) {
            result = result + 0.01f;
        }
        return result;
    }

    public static float getValueFromDegree(float target) {
        float result = 0;
        float counter = 0;
        while (counter < target) {
            result = result - 2.5f;
            counter = counter + 3;
        }

        return result;
    }
}
