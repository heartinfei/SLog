package io.github.heartinfei.slogger.extensions

/**
 *
 * @author Rango on 2019-09-07 wangqiang@smzdm.com
 */
fun String.selfRepeat(times: Int): String {
    var result = ""
    repeat(kotlin.math.max(0, times)) {
        result += this
    }
    return result
}