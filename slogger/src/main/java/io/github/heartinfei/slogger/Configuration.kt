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
class Configuration : Cloneable {
    internal var trackFilter: String? = null
    internal var tag: String? = ""
    internal var trackDeep: Int = 1
    internal var printTrackInfo: Boolean = true
    internal var printThreadInfo: Boolean = true
    internal var printTimeStamp: Boolean = false

    public override fun clone(): Configuration {
        return super.clone() as Configuration
    }

    fun setPrintThreadInfo(arg: Boolean): Configuration {
        this.printThreadInfo = arg
        return this
    }

    fun setPrintTimeStamp(arg: Boolean): Configuration {
        this.printTimeStamp = arg
        return this
    }

    fun setPrintTrackInfo(arg: Boolean): Configuration {
        this.printTrackInfo = arg
        return this
    }

    fun setTrackFilter(arg: String): Configuration {
        this.trackFilter = arg
        return this
    }

    fun setTrackDeep(deep: Int): Configuration {
        this.trackDeep = deep
        return this
    }

    fun setTag(arg: String): Configuration {
        this.tag = arg
        return this
    }
}

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