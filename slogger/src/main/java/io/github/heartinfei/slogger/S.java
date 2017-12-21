package io.github.heartinfei.slogger;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    public static void i(String... msg) {
        DELEGATE.i(getCurrentConfigration(), msg);
    }

    /**
     * 打印异常
     *
     * @param msg
     */
    public static void e(Throwable... msg) {
        DELEGATE.e(getCurrentConfigration(), msg);
    }

    /**
     * 如果当前类不存在用户的自定义配置信息使用默认配置信息
     */
    private static Configuration GLOADBAL_CONF;
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
        public void i(Configuration conf, String... msgs) {
            BasePlan[] plans = plansArray;
            for (BasePlan plan : plans) {
                plan.i(conf, msgs);
            }
        }

        @Override
        public void e(Configuration conf, Throwable... msgs) {
            BasePlan[] plans = plansArray;
            for (BasePlan plan : plans) {
                plan.e(conf, msgs);
            }
        }
    };

    private final static TagWatcher WATCHER = new TagWatcher() {
        @Override
        public void onActivityDestroyed(Activity activity) {
            flush();
            USER_CONF.remove(activity.getClass().getSimpleName());
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


    public static void init(Application app) {
        init(app, null);
    }

    public static void init(@NonNull Application app, Configuration configuration) {
        if (app == null) {
            throw new RuntimeException("Application must be not null!");
        }
        if (configuration == null) {
            GLOADBAL_CONF = new Configuration.Builder(app.getClass())
                    .build();
        } else {
            GLOADBAL_CONF = configuration;
        }

        app.registerActivityLifecycleCallbacks(WATCHER);
    }

    @UiThread
    public static void addConfig(Configuration c) {
        USER_CONF.put(getCurrentKey(), c);
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
        String declaringClass = traceElements[traceElements.length - 1].getClassName();
        int index = declaringClass.indexOf("$");
        return index > 0 ? declaringClass.substring(0, index) : declaringClass;
    }

    private static Configuration getConfigration(String key) {
        return USER_CONF.get(key);
    }

    private static Configuration getCurrentConfigration() {
        Configuration c = getConfigration(getCurrentKey());
        if (c == null) {
            c = GLOADBAL_CONF;
        }
        return c;
    }

}
