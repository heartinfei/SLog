package io.github.heartinfei.slogger.plan;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.heartinfei.slogger.BasePlan;
import io.github.heartinfei.slogger.Configuration;
import io.github.heartinfei.slogger.cache.DiskLruCache;

/**
 * 简介：
 *
 * @author 王强 on 2017/11/24 249346528@qq.com
 */
public class ReleasePlan extends BasePlan {
    /**
     * 减少磁盘访问次数
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


    /**
     * 日志存储线程池
     * 使用线程池减少线程竞争以及数据安全
     */
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
     * @param bufferSizeK
     */
    public ReleasePlan(String mCacheDirPath, int bufferSizeK) {
        this.mCacheDirPath = mCacheDirPath;
        if (bufferSizeK > 0) {
            mBufferSize = bufferSizeK * 1024;
        }
    }

    @Override
    protected void logInfo(@NonNull Configuration c, @Nullable List<String> msg) {
        for (String s : msg) {
            writeBuffer(appendHeaderInfo(c, s));
        }
    }

    @Override
    protected void logErro(@NonNull Configuration c, @Nullable List<String> msg) {
        logInfo(c, msg);
    }

    private String appendHeaderInfo(Configuration c, String msg) {
        return "##" + c.getTag() + "##\n" + msg;
    }

    private void writeBuffer(String logString) {
        int len = logString.length();
        for (int i = 0; i < len; i += mBufferSize) {
            String subString = logString.substring(i, i + Math.min(mBufferSize - 1, len));
            if (subString.length() + mLogBuffer.length() > mBufferSize) {
                flush();
            }
            mLogBuffer.append(subString);
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

    /**
     * 限制单个Log日志文件最大为4M
     *
     * @author 王强 on 2017/12/21 249346528@qq.com
     */
    private static class LogWriter implements Runnable {
        private final static String REX = "(^##(.+)##$)";
        private final static Pattern p = Pattern.compile(REX);
        private final String SP = "\n#################################\n";

        private Map<String, DiskLruCache.Editor> editorMap = new HashMap<>();

        private Map<DiskLruCache.Editor, OutputStream> writerMap = new HashMap<>();
        private String mCacheDirPath;
        private String logs;
        private long MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024;

        public LogWriter(String logs, String dir) {
            this.logs = logs;
            this.mCacheDirPath = dir;
        }

        private DiskLruCache mDiskLruCache;

        @Override
        public void run() {
            try {
                write();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }

        private void write() throws Exception {
            File cacheDir = new File(mCacheDirPath);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir,
                    1,
                    1,
                    MAX_DISK_CACHE_SIZE);

            BufferedReader reader = new BufferedReader(new StringReader(logs));
            String line;
            String tag;
            OutputStream outputStream = null;
            while ((line = reader.readLine()) != null) {
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    tag = m.group(2) + "_log";
                    DiskLruCache.Editor editor = getCachedEditor(mDiskLruCache, tag);
                    outputStream = getCachedOutputStream(editor);
                    outputStream.write(SP.getBytes());
                    editorMap.put(tag, editor);
                    writerMap.put(editor, outputStream);
                    continue;
                }
                if (outputStream != null) {
                    outputStream.write(line.getBytes());
                }
            }
        }

        private OutputStream getCachedOutputStream(DiskLruCache.Editor editor) throws Exception {
            OutputStream outputStream = writerMap.get(editor);
            if (outputStream == null) {
                outputStream = editor.newOutputStream(0);
            }
            return outputStream;
        }

        private DiskLruCache.Editor getCachedEditor(DiskLruCache lruCache, String tag) throws Exception {
            DiskLruCache.Editor editor = editorMap.get(tag);
            if (editor == null) {
                editor = lruCache.edit(tag);
            }
            return editor;
        }

        private void close() {
            Iterator<DiskLruCache.Editor> it = writerMap.keySet().iterator();
            while (it.hasNext()) {
                try {
                    DiskLruCache.Editor editor = it.next();
                    OutputStream outputStream = writerMap.remove(editor);
                    outputStream.flush();
                    outputStream.close();
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                mDiskLruCache.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
