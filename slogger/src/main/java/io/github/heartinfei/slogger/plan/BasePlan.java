package io.github.heartinfei.slogger.plan;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.github.heartinfei.slogger.Configuration;

/**
 * 简介：
 *
 * @author 王强 on 2017/11/23 249346528@qq.com
 */
public abstract class BasePlan {
    /**
     * Android fragment 内置的Log工具类限制输出长度不能超过4k，所以要打印的log信息如果超过4k需要做分块打印处理
     */
    private static final int CHUNK_SIZE = 4000;

    /**
     * 输出Log信息
     *
     * @param c   配置信息
     * @param msg log内容
     */
    protected void logInfo(@NonNull Configuration c, @Nullable List<String> msg) {
    }

    /**
     * 输出错误信息
     *
     * @param c
     * @param msg
     */
    protected void logErro(@NonNull Configuration c, @Nullable List<String> msg) {
    }

    protected void i(Configuration conf, String... msgs) {
        String header = generateLogHeader(conf);
        for (String msg : msgs) {
            logInfo(conf, buildMessage(header, msg, conf));
        }
    }

    protected void e(Configuration conf, Throwable... msgs) {
        for (Throwable t : msgs) {
            logErro(conf, buildMessage(null, getThrowableInfo(t), conf));
        }
    }

    private List<String> buildMessage(String header, @Nullable String msg, @NonNull Configuration config) {
        if (msg == null) {
            msg = "";
        }
        if (header == null) {
            header = "";
        }
        List<String> result = new LinkedList<>();
        //要打印的log 日志的总长度
        int len = header.length() + msg.length();
        if (len < CHUNK_SIZE) {
            result.add(header + msg);
        } else {
            int lastIndex = 0;
            for (int i = CHUNK_SIZE - header.length(); i < len; i += CHUNK_SIZE) {
                result.add(header + msg.substring(lastIndex, i));
                lastIndex += i;
            }
        }
        return result;
    }

    /**
     * 生成Log的头信息
     *
     * @param config 配置信息
     * @return 头信息
     */
    private String generateLogHeader(Configuration config) {
        StringBuilder msgBuilder = new StringBuilder();
        if (config.isShowThreadInfo()) {
            msgBuilder.append(getThreadInfo());
        }
        //当前App的堆栈信息
        List<String> trackInfos =
                getTrackInfo(extractCurrentAppStackTrack(config.getPkgName()), config.getTrackInfoDeep());
        if (config.isPrintLineNo()) {
            msgBuilder.append(trackInfos.remove(0));
        }
        if (config.isPrintTrackInfo()) {
            for (String trackInfo : trackInfos) {
                msgBuilder.append(trackInfo);
            }
        }
        return msgBuilder.toString();
    }

    /**
     * 生成当前App调用关系的堆栈信息
     *
     * @param pkgName 当前App的包名
     * @return 堆栈信息
     */
    private List<StackTraceElement> extractCurrentAppStackTrack(String pkgName) {
        List<StackTraceElement> result = new ArrayList<>();
        try {
            for (StackTraceElement traceElement : Thread.currentThread().getStackTrace()) {
                if (traceElement.getClassName().contains(pkgName)) {
                    result.add(traceElement);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成堆栈信息
     *
     * @param traceElements
     * @param deep
     * @return
     */
    private List<String> getTrackInfo(@NonNull List<StackTraceElement> traceElements, int deep) {
        List<String> result = new ArrayList<>();
        String offset = "";
        deep = deep >= traceElements.size() ? traceElements.size() : 0;
        for (int i = 0; i < deep; i++) {
            StackTraceElement traceElement = traceElements.get(i);
            result.add(offset + traceElement.toString() + "\n");
            offset += "↳";
        }
        return result;
    }

    /**
     * 获取当前线程的信息
     *
     * @return 当前线程的名称
     */
    private String getThreadInfo() {
        return "Thread:" + Thread.currentThread().getName() + "#";
    }

    /**
     * 提取异常信息
     *
     * @param t 异常
     * @return 信息
     */
    private String getThrowableInfo(Throwable t) {
        StringWriter sw = new StringWriter(1024);
        PrintWriter pw = new PrintWriter(sw, false);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
