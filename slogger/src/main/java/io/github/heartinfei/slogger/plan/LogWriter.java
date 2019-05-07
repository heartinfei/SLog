package io.github.heartinfei.slogger.plan;

/**
 * @author 王强 on 2017/12/22 249346528@qq.com
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 限制单个Log日志文件最大为4M不实用DiskLruCache
 *
 * @author 王强 on 2017/12/21 249346528@qq.com
 */
class LogWriter implements Runnable {
    private final static String REX = "(^##(.+)##$)";
    private final static Pattern P_N = Pattern.compile(REX);
    private static final String P_D = "yyyy-MM-dd HH:mm:ss";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(P_D, Locale.CHINA);
    private final String SP = "\n##############%s###################\n";

    private Map<String, FileWriter> writerMap = new ConcurrentHashMap<>();
    private String mCacheDirPath;
    private String logs;
    /**
     * Log文件的最大尺寸
     */
    private final long MAX_LOG_FILE_LENGTH = 4 * 1024 * 1024;

    public LogWriter(String logs, String dir) {
        this.logs = logs;
        this.mCacheDirPath = dir;
    }

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

    private void write() {
        File cacheDir = new File(mCacheDirPath);
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            throw new RuntimeException("Create cache dir fail");
        }
        BufferedReader reader = new BufferedReader(new StringReader(logs));
        String line;
        String tag = null;
        FileWriter writer = null;
        try {
            while ((line = reader.readLine()) != null) {
                try {
                    Matcher m = P_N.matcher(line);
                    if (m.matches()) {
                        tag = m.group(2);
                        writer = getWriter(tag);
                        writer.write(String.format(SP, getPrintDate()));
                        writerMap.put(tag, writer);
                        continue;
                    }
                    if (writer != null) {
                        writer.write(line + "\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (tag != null) {
                        writerMap.remove(tag);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPrintDate() {
        return dateFormat.format(new Date());
    }

    private FileWriter getWriter(String tag) throws Exception {
        FileWriter writer = writerMap.get(tag);
        if (writer == null) {
            File cacheFile = getCacheFile(tag);
            writer = new FileWriter(cacheFile, true);
        }
        return writer;
    }

    private String getCacheFileNameByTag(String tag) {
        return tag + ".log";
    }

    private File getCacheFile(String tag) throws Exception {
        String fileName = getCacheFileNameByTag(tag);
        File cacheFile = new File(mCacheDirPath, fileName);
        if (cacheFile.exists()) {
            return verifyFileSize(cacheFile);
        } else {
            return cacheFile;
        }
    }

    private File verifyFileSize(File f) throws Exception {
        File cacheFile = f;
        long len = cacheFile.length();
        if (len >= MAX_LOG_FILE_LENGTH) {
            return resizeFile(f);
        } else {
            return cacheFile;
        }
    }

    /**
     * 单个日志文件大小限制
     *
     * @param cacheFile 日志缓存文件
     * @return
     * @throws Exception 文件处理失败
     */
    private File resizeFile(File cacheFile) throws Exception {
        File f = cacheFile;
        File bak = new File(f.getParent(), f.getName() + ".bak");
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(f);
            fo = new FileOutputStream(bak);
            in = fi.getChannel();
            out = fo.getChannel();
            long count = MAX_LOG_FILE_LENGTH / 2;
            long position = in.size() - count;
            in.transferTo(position, count, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (f.delete() && bak.renameTo(f)) {
            return f;
        } else {
            throw new RuntimeException("压缩日志文件失败！");
        }
    }

    private void close() {
        Iterator<String> it = writerMap.keySet().iterator();
        while (it.hasNext()) {
            String tag = it.next();
            FileWriter writer = writerMap.remove(tag);
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }//end close
}