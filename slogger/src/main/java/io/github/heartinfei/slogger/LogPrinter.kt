package io.github.heartinfei.slogger

import io.github.heartinfei.slogger.plan.BasePlan
import java.util.*

/**
 *
 * @author Rango on 2019-05-29 249346528@qq.com
 */
interface LogPrinter {
    /** Force read from main memory.*/
    val plans: ArrayList<BasePlan>

    fun i(message: String?, vararg args: Any?)
    fun d(message: String?, vararg args: Any?)
    fun e(message: String?, vararg args: Any?)
    fun json(message: String?)
}