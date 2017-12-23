package io.github.heartinfei.slogger.plan;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.heartinfei.slogger.BasePlan;

/**
 * 简介：
 *
 * @author 王强 on 2017/11/24 249346528@qq.com
 */
public class ReleasePlan extends BasePlan {
    /**
     * 减少磁盘访问次数 2M
     */
    private final int DEFAULT_BUFFER_SIZE = 1024 * 1024 * 2;
    /**
     * 日志文件输出目录
     */
    private final String mCacheDirPath;

    /**
     * 日志缓冲区
     */
    private final StringBuffer mLogBuffer = new StringBuffer();

    /**
     * 日志缓冲区的默认大小
     */
    private int mBufferSize = DEFAULT_BUFFER_SIZE;


    private ExecutorService writeExecutor = Executors.newSingleThreadExecutor();


    /**
     * @param mCacheDirPath
     */
    public ReleasePlan(String mCacheDirPath) {
        this.mCacheDirPath = mCacheDirPath;
        this.mBufferSize = DEFAULT_BUFFER_SIZE;
    }

    /**
     * @param mCacheDirPath
     * @param bufferSize
     */
    public ReleasePlan(String mCacheDirPath, int bufferSize) {
        this.mCacheDirPath = mCacheDirPath;
        if (bufferSize > 0) {
            mBufferSize = bufferSize;
        }
    }

    @Override
    protected void logInfo(@NonNull String tag, @Nullable List<String> msg) {
        for (String s : msg) {
            writeBuffer(appendHeaderInfo(tag, s));
        }
    }

    @Override
    protected void logError(@NonNull String tag, @Nullable List<String> msg) {
        logInfo(tag, msg);
    }

    private String appendHeaderInfo(String tag, String msg) {
        return "##" + tag + "##\n" + msg;
    }

    private void writeBuffer(String logString) {
        int len = logString.length();
        for (int i = 0; i < len; i += mBufferSize) {
            String subString = logString.substring(i, i + Math.min(mBufferSize - 1, len));
            if (subString.length() + mLogBuffer.length() > mBufferSize) {
                flush();
            }
            mLogBuffer.append(subString).append("\n");
        }
    }


    /**
     * 刷新缓冲区将缓冲区内容输出到文件
     *
     * @return true
     */
    @Override
    public boolean flush() {
        writeExecutor.execute(new LogWriter(mLogBuffer.toString(), mCacheDirPath));
        mLogBuffer.setLength(0);
        return super.flush();
    }
}
