package io.github.heartinfei.superlog

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import io.github.heartinfei.slogger.S
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

/**
 * 简介：Demo
 *
 * @author 王强 on 2017/11/23 249346528@qq.com
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnCustomTag.setOnClickListener {
            S.withTag("MyTag").i((it as TextView).text)
        }

        btnStackInfo.setOnClickListener {
            testTrack1()
        }

        btnThreadInfo.setOnClickListener {
            threadFun()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_print -> {
                People().say()
            }
        }
    }

    private fun testTrack1() {
        testTrack2()
    }

    private fun testTrack2() {
        testTrack3()
    }

    private fun testTrack3() {
        S.withTrackInfo(true).i("Hello Track")
    }

    private fun threadFun() {
        thread(start = true, name = "MyThread") {
            People().say()
        }
    }
}

class People {
    fun say() {
        S.withThreadInfo(true).i("Hello thread.")
    }
}
