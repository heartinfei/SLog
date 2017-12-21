package io.github.heartinfei.superlog;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import io.github.heartinfei.slogger.Configuration;
import io.github.heartinfei.slogger.plan.DebugPlan;
import io.github.heartinfei.slogger.S;
import io.github.heartinfei.slogger.plan.ReleasePlan;

/**
 * 简介：
 *
 * @author 王强 on 2017/11/23 249346528@qq.com
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        S.addPlant(new DebugPlan());
        String path = Environment.getExternalStorageDirectory().getPath()+"/SuperLog/";
        S.addPlant(new ReleasePlan(path));
        Configuration c = new Configuration.Builder(this.getClass())
                .isPrintTrackInfo(true)
                .trackInfoDeep(10)
                .tag("Fucking")
                .build();
        S.init(getApplication(), c);
        S.i(getClass().getName());
        S.i(getClass().getSimpleName());
    }

    int i = 0;

    @Override
    public void onClick(View v) {
//        test();
//        S.e(new RuntimeException("Fuck"));
        S.i("测试" + i++);
        S.flush();
    }

    private void test() {
        S.i("hello");
    }

    public static void main(String arg[]) {
        System.out.println();
    }

    private static void test(int i) {
        String tag = "我是" + i;
        for (int j = 0; j < 100; j++) {
            Log.i(tag, "只输出" + i);
        }
    }


    private void printInfo() {
        Thread c = Thread.currentThread();
        StackTraceElement es[] = Thread.currentThread().getStackTrace();
        /*Log.i("S_LOG",es[0].toString());
        Log.i("S_LOG","--------------------");
        Log.i("S_LOG",es[1].toString());
        Log.i("S_LOG","--------------------");
        Log.i("S_LOG",es[2].toString());
        Log.i("S_LOG","--------------------");*/
        Log.i("(MainActivity.java:", "io.github.heartinfei.superlog.MainActivity.printInfo(MainActivity.java:26)");
        for (StackTraceElement e : es) {
            Log.i("S_LOG", e.toString());
            Log.i("S_LOG", "--------------------");
        }

    }


}
