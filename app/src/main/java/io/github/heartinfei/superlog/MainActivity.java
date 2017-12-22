package io.github.heartinfei.superlog;

import android.content.Intent;
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
 * 简介：Demo
 *
 * @author 王强 on 2017/11/23 249346528@qq.com
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_print:
                testPrint(5);
                break;
            case R.id.btn_test:
                startActivity(new Intent(this, TestActivity.class));
                break;
        }
    }

    private void testPrint(int count) {
        for (int i = 0; i < count; i++) {
            S.i("Slog test:" + i);
        }
    }
}
