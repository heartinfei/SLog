package io.github.heartinfei.slogger

/**
 *
 * @author Rango on 2019-05-29 249346528@qq.com
 */
internal interface LogPrinter {
    fun i(message: String?, vararg args: Any?)
    fun d(message: String?, vararg args: Any?)
    fun e(message: String?, vararg args: Any?)
    fun json(message: String?)
}