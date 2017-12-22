package io.github.heartinfei.slogger;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.heartinfei.slogger.plan.TagWatcher;

/**
 * 简介：日志打印工具类
 *
 * @author 王强 on 2017/11/23 249346528@qq.com
 */
public class S {
    /**
     * 打印消息
     *
     * @param msg 消息
     */
    public static void i(Object... msg) {
        log(null, msg);
    }

    public static void log(String tag, Object... msg) {
        DELEGATE.i(getCurrentConfigration(), tag, msg);
    }

    /**
     * 打印异常
     *
     * @param msg
     */
    public static void e(Throwable... msg) {
        log(null, msg);
    }

    public static void log(String tag, Throwable... msg) {
        DELEGATE.e(getCurrentConfigration(), tag, msg);
    }

    /**
     * 如果当前类不存在用户的自定义配置信息使用默认配置信息
     */
    private static Configuration GLOBAL_CONF;
    /**
     * 用户自定义的Log配置信息
     * key 类名
     * value Configuration
     */
    private final static Map<String, Configuration> USER_CONF = new ConcurrentHashMap<>();
    private final static List<BasePlan> PLANS = Collections.synchronizedList(new LinkedList<BasePlan>());
    private static BasePlan[] plansArray = new BasePlan[0];
    private static final BasePlan DELEGATE = new BasePlan() {
        @Override
        public void i(Configuration conf, String tag, Object... msgs) {
            BasePlan[] plans = plansArray;
            for (BasePlan plan : plans) {
                plan.i(conf, tag, msgs);
            }
        }

        @Override
        public void e(Configuration conf, String tag, Throwable... msgs) {
            BasePlan[] plans = plansArray;
            for (BasePlan plan : plans) {
                plan.e(conf, tag, msgs);
            }
        }
    };

    private final static TagWatcher WATCHER = new TagWatcher() {
        @Override
        public void onActivityDestroyed(Activity activity) {
            flush();
            removeConfig(activity.getClass());
        }
    };

    public static void flush() {
        BasePlan[] plans = plansArray;
        for (BasePlan plan : plans) {
            plan.flush();
        }
    }

    private S() {
        throw new AssertionError("No instances.");
    }


    public static void init(Context app) {
        init(app, null);
    }

    public static synchronized void init(@NonNull Context app, Configuration configuration) {
        if (app == null) {
            throw new RuntimeException("Application must be not null!");
        }
        if (configuration == null) {
            GLOBAL_CONF = new Configuration.Builder(app)
                    .build();
        } else {
            GLOBAL_CONF = configuration;
        }

        ((Application) app.getApplicationContext()).registerActivityLifecycleCallbacks(WATCHER);
    }

    @UiThread
    public static void addConfig(Configuration c) {
        USER_CONF.put(c.getTargetContextName(), c);
    }

    public static void removeConfig(Class<?> cls) {
        USER_CONF.remove(cls.getName());
    }

    public static void addPlant(BasePlan... plans) {
        if (plans == null) {
            throw new NullPointerException("trees == null");
        }
        for (BasePlan plan : plans) {
            if (plan == null) {
                throw new NullPointerException("contains null");
            }
            PLANS.add(plan);
        }
        plansArray = PLANS.toArray(new BasePlan[PLANS.size()]);
    }

    public static void removePlan(BasePlan plan) {
        PLANS.remove(plan);
        plansArray = PLANS.toArray(new BasePlan[PLANS.size()]);
    }

    private static String getCurrentKey() {
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        String declaringClass = null;
        for (int i = traceElements.length - 1; i >= 0; i--) {
            if (GLOBAL_CONF == null) {
                throw new RuntimeException("Global Configuration is null. Use S.init(...) first.");
            }
            declaringClass = traceElements[i].getClassName();
            if (declaringClass.contains(GLOBAL_CONF.getPkgName())) {
                int index = declaringClass.indexOf("$");
                return index > 0 ? declaringClass.substring(0, index) : declaringClass;
            }
        }
        return "";
    }

    private static Configuration getConfigration(String key) {
        return USER_CONF.get(key);
    }

    private static Configuration getCurrentConfigration() {
        Configuration c = getConfigration(getCurrentKey());
        if (c == null) {
            c = GLOBAL_CONF;
        }
        return c;
    }

}
