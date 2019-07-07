package io.github.heartinfei.slogger

import android.util.Log
import io.github.heartinfei.slogger.plan.BasePlan
import java.util.*
import kotlin.collections.ArrayList

/**
 * Log helper.
 * @author Rango on 2019-05-28 249346528@qq.com
 */
class S private constructor() {
    init {
        throw AssertionError()
    }

    companion object : LogPrinter {
        override val plans: ArrayList<BasePlan> = ArrayList()

        private var CONFIG: SConfiguration? = null

        /** Force read from main memory.*/
        @Volatile
        internal var planArray = emptyArray<BasePlan>()

        @JvmStatic
        fun init(c: SConfiguration): Companion {
            if (this.CONFIG != null) {
                throw RuntimeException("S is already init.")
            }
            this.CONFIG = c
            return this
        }

        @JvmStatic
        fun withTag(tag: String): LogPrinterProxy {
            if (CONFIG == null) {
                throw RuntimeException("Init config is null call S.init(...) first.")
            }
            val config = CONFIG!!.clone().apply {
                this.tag = tag
            }
            return LogPrinterProxy(config, this)
        }

        @JvmStatic
        fun withTrackFilter(filter: String): LogPrinterProxy {
            if (CONFIG == null) {
                throw RuntimeException("Init config is null call S.init(...) first.")
            }
            val config = CONFIG!!.clone().apply {
                this.trackFilter = filter
            }
            return LogPrinterProxy(config, this)
        }

        @JvmStatic
        fun withTrackDeep(level: Int): LogPrinterProxy {
            if (CONFIG == null) {
                throw RuntimeException("Init config is null call S.init(...) first.")
            }
            val config = CONFIG!!.clone().apply {
                this.trackDeep = level
            }
            return LogPrinterProxy(config, this)
        }

        @JvmStatic
        fun withTrackInfo(stat: Boolean): LogPrinterProxy {
            if (CONFIG == null) {
                throw RuntimeException("Default 'SConfiguration' is null,please call S.init() first.")
            }

            val config = CONFIG!!.clone().apply {
                this.printTrackInfo = stat
            }
            return LogPrinterProxy(config, this)
        }

        @JvmStatic
        fun withThreadInfo(print: Boolean): LogPrinterProxy {
            if (CONFIG == null) {
                throw RuntimeException("Default 'SConfiguration' is null,please call S.init() first.")
            }
            val config = CONFIG!!.clone().apply {
                this.printThreadInfo = print
            }
            return LogPrinterProxy(config, this)
        }

        @JvmStatic
        fun addPlans(vararg plans: BasePlan) {
            if (CONFIG == null) {
                throw RuntimeException("Default 'SConfiguration' is null,please call S.init() first.")
            }
            synchronized(this.plans) {
                Collections.addAll(this.plans, *plans)
                planArray = this.plans.toTypedArray()
            }
        }

        @JvmStatic
        fun removePlan(plan: BasePlan) {
            synchronized(plans) {
                require(plans.remove(plan)) { "Cannot uproot tree which is not planted: $plan" }
                planArray = plans.toTypedArray()
            }
        }

        @JvmStatic
        fun clearPlans(): Companion {
            plans.clear()
            planArray = plans.toTypedArray()
            return this
        }

        @JvmStatic
        override fun i(message: String?, vararg args: Any?) {
            planArray.forEach { it.assembleAndEchoLog(CONFIG, Log.INFO, message, *args) }
        }

        @JvmStatic
        override fun d(message: String?, vararg args: Any?) {
            planArray.forEach { it.assembleAndEchoLog(CONFIG, Log.DEBUG, message, *args) }
        }

        override fun e(message: String?, vararg args: Any?) {
            planArray.forEach { it.assembleAndEchoLog(CONFIG, Log.ERROR, message, *args) }
        }

        override fun json(message: String?) {
            throw RuntimeException("Do not implement yet.")
        }
    }
}