package io.github.heartinfei.slogger

import android.util.Log
import io.github.heartinfei.slogger.plan.BasePlan
import java.lang.RuntimeException
import java.util.*

/**
 * Log helper.
 * @author Rango on 2019-05-28 249346528@qq.com
 */
class S private constructor() {
    init {
        throw AssertionError()
    }

    companion object : LogPrinter {
        private var CONFIG: Configuration? = null

        private val plans = ArrayList<BasePlan>()

        /** Force read from main memory.*/
        @Volatile
        internal var planArray = emptyArray<BasePlan>()

        @JvmStatic
        fun init(c: Configuration): Companion {
            if (this.CONFIG != null) {
                throw RuntimeException("S is already init.")
            }
            this.CONFIG = c
            return this
        }

        @JvmStatic
        fun withTrackFilter(filter: String): Configuration {
            if (CONFIG == null) {
                throw RuntimeException("")
            }
            return Configuration(CONFIG!!).setTrackFilter(filter)
        }

        @JvmStatic
        fun withTag(tag: String): Configuration {
            if (CONFIG == null) {
                throw RuntimeException("")
            }
            return Configuration(CONFIG!!).setTag(tag)
        }

        @JvmStatic
        fun withTrackDeep(level: Int): Configuration {
            if (CONFIG == null) {
                throw RuntimeException("")
            }
            return Configuration(CONFIG!!).setTrackDeep(level)
        }

        @JvmStatic
        fun withThreadInfo(print: Boolean): Configuration {
            if (CONFIG == null) {
                throw RuntimeException("")
            }
            return Configuration(CONFIG!!).setPrintThreadInfo(print)
        }

        @JvmStatic
        fun addPlans(vararg plans: BasePlan) {
            if (CONFIG == null) {
                throw RuntimeException("Default 'Configuration' is null,please call S.init() first.")
            }
            for (plan in plans) {
                requireNotNull(plan)
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

        internal fun invokePlans(config: Configuration, priority: Int, message: String?, vararg args: Any?) {
            planArray.forEach { it.assembleAndEchoLog(config, priority, message, *args) }
        }
    }
}