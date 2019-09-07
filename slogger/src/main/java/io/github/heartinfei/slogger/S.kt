package io.github.heartinfei.slogger

import android.util.Log
import io.github.heartinfei.slogger.formter.ContentFormat
import io.github.heartinfei.slogger.formter.JSONFormat
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
        private val PLANS: ArrayList<BasePlan> = ArrayList()
        private val CONTENT_FORMAT: ContentFormat = JSONFormat()
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
                throw RuntimeException("Init config is null, Call S.init(...) first.")
            }
            val config = CONFIG!!.clone().apply {
                this.tag = tag
            }
            return LogPrinterProxy(config, this)
        }

        @JvmStatic
        fun withTrackFilter(filter: String): LogPrinterProxy {
            if (CONFIG == null) {
                throw RuntimeException("Init config is null, Call S.init(...) first.")
            }
            val config = CONFIG!!.clone().apply {
                this.trackFilter = filter
            }
            return LogPrinterProxy(config, this)
        }

        @JvmStatic
        fun withTrackDeep(level: Int): LogPrinterProxy {
            if (CONFIG == null) {
                throw RuntimeException("Init config is null, Call S.init(...) first.")
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
            synchronized(this.PLANS) {
                Collections.addAll(this.PLANS, *plans)
                planArray = this.PLANS.toTypedArray()
            }
        }

        @JvmStatic
        fun removePlan(plan: BasePlan) {
            synchronized(PLANS) {
                require(PLANS.remove(plan)) { "Cannot remove plan which is not added: $plan" }
                planArray = PLANS.toTypedArray()
            }
        }

        @JvmStatic
        fun clearPlans(): Companion {
            PLANS.clear()
            planArray = PLANS.toTypedArray()
            return this
        }

        @JvmStatic
        override fun i(message: Any?) {
            i(message, null)
        }


        internal fun i(message: Any?, c: SConfiguration?) {
            message.let {
                return@let when (it) {
                    is String -> it
                    else -> it.toString()
                }
            }.apply {
                if (c == null) {
                    planArray.forEach { it.assembleAndEchoLog(CONFIG, Log.INFO, this) }
                } else {
                    planArray.forEach { it.assembleAndEchoLog(c, Log.INFO, this) }
                }
            }
        }

        @JvmStatic
        override fun d(message: Any?) {
            d(message, null)
        }


        internal fun d(message: Any?, c: SConfiguration?) {
            message.let {
                return@let when (it) {
                    is String -> it
                    else -> it.toString()
                }

            }.apply {
                if (c == null) {
                    planArray.forEach { it.assembleAndEchoLog(CONFIG, Log.DEBUG, this) }
                } else {
                    planArray.forEach { it.assembleAndEchoLog(c, Log.DEBUG, this) }
                }
            }
        }

        @JvmStatic
        override fun e(message: Any?) {
            e(message, null)
        }

        internal fun e(message: Any?, c: SConfiguration?) {
            message.let {
                return@let when (it) {
                    is String -> it
                    else -> it.toString()
                }
            }.apply {
                if (c == null) {
                    planArray.forEach { it.assembleAndEchoLog(CONFIG, Log.ERROR, this) }
                } else {
                    planArray.forEach { it.assembleAndEchoLog(c, Log.ERROR, this) }
                }
            }
        }

        @JvmStatic
        override fun json(message: String?) {
            json(message, null)
        }

        internal fun json(message: String?, config: SConfiguration?) {
            i(CONTENT_FORMAT.formatString(message), config)
        }
    }
}