package io.github.heartinfei.superlog;

import android.app.Application;
import android.os.Environment;

import io.github.heartinfei.slogger.Configuration;
import io.github.heartinfei.slogger.S;
import io.github.heartinfei.slogger.plan.DebugPlan;
import io.github.heartinfei.slogger.plan.ReleasePlan;

/**
 * @author 王强 on 2017/12/22 249346528@qq.com
 */
public class SApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String path = Environment.getExternalStorageDirectory().getPath() + "/SuperLog/";
        Configuration config = new Configuration.Builder(this)
                .trackInfoDeep(Integer.MAX_VALUE) //打印堆栈深度
                //确保你的包名和源码包一致,如果你的程序存在多个构建这里需要注意否则堆栈信息可能不正确
                .pkgName(BuildConfig.APPLICATION_ID)
                .tag("S_LOG")           //default is Application name
                .isPrintLineNo(true)    //打印行号 defaut true
                .isPrintTag(true)       //打印Tag defaut true
                .isPrintTrackInfo(true) //打印堆栈 defaut true
                .isPrintThreadInfo(true)//打印线程信息 defaut true
                .build();
        S.init(this, config);
        if (BuildConfig.DEBUG) {
            S.addPlant(new DebugPlan());    //输出到控制台
        } else {
            S.addPlant(new ReleasePlan(path)); //输出到文件
        }
    }
}
