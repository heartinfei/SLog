package io.github.heartinfei.slogger

import android.util.Log

/**
 * [trackFilter] Used to filter stack
 * [tag]    Log withTag
 * [trackDeep] The stack deep to print.
 * [isPrintStackInfo] true print stack info.
 * [isPrintThreadInfo] true print thread info
 * [isPrintTimeStamp] true print timestamp.
 * @author Rango on 2019-05-29 249346528@qq.com
 */
class Configuration(
        internal var trackFilter: String? = null,
        internal var tag: String? = "",
        internal var trackDeep: Int = 1,
        internal var isPrintStackInfo: Boolean = true,
        internal var isPrintThreadInfo: Boolean = true,
        internal var isPrintTimeStamp: Boolean = false
) : LogPrinter {

    constructor(c: Configuration) : this(
            trackFilter = c.trackFilter,
            tag = c.tag,
            trackDeep = c.trackDeep,
            isPrintStackInfo = c.isPrintStackInfo,
            isPrintThreadInfo = c.isPrintThreadInfo,
            isPrintTimeStamp = c.isPrintTimeStamp
    )

    fun setPrintTimeStamp(print: Boolean): Configuration {
        isPrintTimeStamp = print
        return this
    }

    fun setTrackFilter(trackFilter: String?): Configuration {
        this.trackFilter = trackFilter
        return this
    }

    fun setTag(tag: String): Configuration {
        this.tag = tag
        return this
    }

    fun setPrintTrackInfo(print: Boolean): Configuration {
        this.isPrintStackInfo = print
        return this
    }

    fun setTrackDeep(level: Int): Configuration {
        this.trackDeep = level
        return this
    }

    fun setPrintThreadInfo(print: Boolean): Configuration {
        this.isPrintThreadInfo = print
        return this
    }

    override fun i(message: String?, vararg args: Any?) {
        S.invokePlans(this, Log.INFO, message, *args)
    }

    override fun d(message: String?, vararg args: Any?) {
        S.invokePlans(this, Log.DEBUG, message, *args)
    }

    override fun e(message: String?, vararg args: Any?) {
        S.invokePlans(this, Log.ERROR, message, *args)
    }

    override fun json(message: String?) {

    }
}