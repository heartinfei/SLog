package io.github.heartinfei.slogger.plan;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * @author 王强 on 2017/12/21 249346528@qq.com
 */

public abstract class TagWatcher implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }
}
