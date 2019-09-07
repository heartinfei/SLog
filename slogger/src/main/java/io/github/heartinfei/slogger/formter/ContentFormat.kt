package io.github.heartinfei.slogger.formter

/**
 *
 * @author Rango on 2019-09-07 wangqiang@smzdm.com
 */
interface ContentFormat {
    fun formatString(content: String?): String
}