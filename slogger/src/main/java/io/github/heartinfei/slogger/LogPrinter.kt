package io.github.heartinfei.slogger

/**
 *
 * @author Rango on 2019-05-29 249346528@qq.com
 */
interface LogPrinter {
    /** Force read from main memory.*/
    fun i(message: Any?)

    fun d(message: Any?)
    fun e(message: Any?)
    fun json(message: String?)
}