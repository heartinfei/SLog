package io.github.heartinfei.superlog;

import android.app.Application;
import android.os.Environment;

import io.github.heartinfei.slogger.Configuration;
import io.github.heartinfei.slogger.S;
import io.github.heartinfei.slogger.plan.BasePlan;
import io.github.heartinfei.slogger.plan.DebugPlan;

/**
 * @author 王强 on 2017/12/22 249346528@qq.com
 */
public class SApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Configuration debugConfig = new Configuration()
                    .setPrintThreadInfo(true)
                    .setPrintTimeStamp(true)
                    .setPrintTrackInfo(true)
                    .setTrackFilter("io.github.heartinfei.superlog");
            S.init(debugConfig).addPlans(new DebugPlan());
        } else {
            Configuration releaseConfig = new Configuration()
                    .setPrintThreadInfo(true)
                    .setPrintTimeStamp(true)
                    .setPrintTrackInfo(true)
                    .setTrackFilter("io.github.heartinfei.superlog");
            S.init(releaseConfig).addPlans(new ReleasePlan());
        }
    }

    class ReleasePlan extends BasePlan {
        @Override
        protected void echoLog(int priority, String tag, String message) {

        }
    }
}
