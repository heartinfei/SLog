package io.github.heartinfei.slogger.plan;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import io.github.heartinfei.slogger.BasePlan;
import io.github.heartinfei.slogger.Configuration;

/**
 * 简介：
 *
 * @author 王强 on 2017/11/24 249346528@qq.com
 */
public class DebugPlan extends BasePlan {
    @Override
    protected void logInfo(@NonNull String tag, @Nullable List<String> msgs) {
        for (String msg : msgs) {
            Log.i(tag, msg);
        }
    }

    @Override
    protected void logError(@NonNull String tag, @Nullable List<String> msgs) {
        for (String msg : msgs) {
            Log.e(tag, msg);
        }
    }
}
