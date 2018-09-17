package practice.mvp.com.manchassignment;

import android.app.Application;

import practice.mvp.com.manchassignment.utilities.ContextHolder;

/**
 * Created by admin on 9/17/18.
 */

public class ManchApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ContextHolder.getInstance().setContext(this);
    }
}
