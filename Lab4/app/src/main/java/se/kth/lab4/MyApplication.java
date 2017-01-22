package se.kth.lab4;

import android.app.Application;

/**
 * Created by Daniel on 2017-01-22.
 */

public class MyApplication extends Application {

    private static boolean isVisible;

    public static boolean isActivityVisible(){
        return isVisible;
    }

    public static void activityResumed() {
        isVisible = true;
    }

    public static void activityPaused(){
        isVisible = false;
    }
}
