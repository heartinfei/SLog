package io.github.heartinfei.superlog

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

import io.github.heartinfei.slogger.S

/**
 * 简介：Demo
 *
 * @author 王强 on 2017/11/23 249346528@qq.com
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_print -> {
                People().say()
                testPrint(5)
            }
            R.id.btn_test -> {
                val intent = Intent(this, TestActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun testPrint(count: Int) {
        for (i in 0 until count) {
            S.i("Slog test:$i")
        }
    }
}

class People {
    fun say() {
        S.i("Hello People.")
    }
}
