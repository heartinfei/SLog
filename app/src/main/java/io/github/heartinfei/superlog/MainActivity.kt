package io.github.heartinfei.superlog

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.heartinfei.slogger.S
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

/**
 * 简介：Demo
 *
 * @author 王强 on 2017/11/23 249346528@qq.com
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_print.setOnClickListener {
            S.json(jsonString())
        }
        btnCustomTag.setOnClickListener {
            S.withTag("MyTag")
                    .withPrintThreadInfo(true)
                    .withPrintTrackInfo(true)
                    .withTrackDeep(1)
                    .withTrackFilter(BuildConfig.APPLICATION_ID)
                    .i("I'm a log.")
        }

        btnStackInfo.setOnClickListener {
            testTrack1()
        }

        btnThreadInfo.setOnClickListener {
            thread(start = true, name = "MyThread") {
                S.withThreadInfo(true).i("I'm a log.")
            }
        }
    }

    private fun jsonString(): String {
        return """[{"name":"rango","age":1,"child":{"name":"yangyang","age":1},"sex":"男"},{"name":"rango","age":1,"child":{"name":"yangyang","age":1}}]"""
    }


    private fun testTrack1() {
        testTrack2()
    }

    private fun testTrack2() {
        testTrack3()
    }

    private fun testTrack3() {
        S.withTrackInfo(true).i("I'm a log.")
    }

    private fun threadFun() {
        thread(start = true, name = "MyThread") {
            People().say()
        }
    }
}

class People {
    fun say() {
        S.withThreadInfo(true).i("I'm a log.")
    }
}
