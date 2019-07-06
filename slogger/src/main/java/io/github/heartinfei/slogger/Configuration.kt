package io.github.heartinfei.slogger

/**
 * [trackFilter] Used to filter stack
 * [tag]    Log withTag
 * [trackDeep] The stack deep to print.
 * [printTrackInfo] true print stack info.
 * [printThreadInfo] true print thread info
 * [printTimeStamp] true print timestamp.
 * @author Rango on 2019-05-29 249346528@qq.com
 */
class Configuration(
        var trackFilter: String? = null,
        var tag: String? = "",
        var trackDeep: Int = 1,
        var printTrackInfo: Boolean = true,
        var printThreadInfo: Boolean = true,
        var printTimeStamp: Boolean = false) {

    constructor(c: Configuration) : this(
            trackFilter = c.trackFilter,
            tag = c.tag,
            trackDeep = c.trackDeep,
            printTrackInfo = c.printTrackInfo,
            printThreadInfo = c.printThreadInfo,
            printTimeStamp = c.printTimeStamp
    )

    fun printThreadInfo(arg: Boolean): Configuration {
        this.printThreadInfo = arg
        return this
    }

    fun printTimeStamp(arg: Boolean): Configuration {
        this.printTimeStamp = arg
        return this
    }

    fun printTrackInfo(arg: Boolean): Configuration {
        this.printTrackInfo = arg
        return this
    }

    fun trackFilter(arg: String): Configuration {
        this.trackFilter = arg
        return this
    }

    fun trackDeep(deep: Int): Configuration {
        this.trackDeep = deep
        return this
    }

    fun tag(arg: String): Configuration {
        this.tag = arg
        return this
    }
}

/**
 * 链式调用封装
 */
class LogPrinterProxy(private val config: Configuration,
                      private val printer: LogPrinter) : LogPrinter by printer {

    fun setPrintTimeStamp(print: Boolean): LogPrinterProxy {
        config.printTimeStamp = print
        return this
    }

    fun setTrackFilter(trackFilter: String?): LogPrinterProxy {
        config.trackFilter = trackFilter
        return this
    }

    fun setTag(tag: String): LogPrinterProxy {
        config.tag = tag
        return this
    }

    fun setPrintTrackInfo(print: Boolean): LogPrinterProxy {
        config.printTrackInfo = print
        return this
    }

    fun setTrackDeep(level: Int): LogPrinterProxy {
        config.trackDeep = level
        return this
    }

    fun setPrintThreadInfo(print: Boolean): LogPrinterProxy {
        config.printThreadInfo = print
        return this
    }
}