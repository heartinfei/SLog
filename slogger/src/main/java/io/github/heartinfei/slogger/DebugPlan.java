package io.github.heartinfei.slogger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

/**
 * 简介：
 *
 * @author 王强 on 2017/11/24 249346528@qq.com
 */
public class DebugPlan extends BasePlan {
    @Override
    protected void logOut(@NonNull String tag, @Nullable List<String> msgs) {
        for (String msg : msgs) {
            Log.i(tag, msg);
        }
    }
}
