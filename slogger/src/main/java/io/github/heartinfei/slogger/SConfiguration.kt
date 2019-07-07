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
class SConfiguration : Cloneable {
    internal var trackFilter: String? = null
    internal var tag: String? = ""
    internal var trackDeep: Int = Int.MAX_VALUE
    internal var printTrackInfo: Boolean = true
    internal var printThreadInfo: Boolean = true

    public override fun clone(): SConfiguration {
        return super.clone() as SConfiguration
    }

    fun setPrintThreadInfo(arg: Boolean): SConfiguration {
        this.printThreadInfo = arg
        return this
    }

    fun setPrintTrackInfo(arg: Boolean): SConfiguration {
        this.printTrackInfo = arg
        return this
    }

    fun setTrackFilter(arg: String): SConfiguration {
        this.trackFilter = arg
        return this
    }

    fun setTrackDeep(deep: Int): SConfiguration {
        this.trackDeep = deep
        return this
    }

    fun setTag(arg: String): SConfiguration {
        this.tag = arg
        return this
    }
}

class LogPrinterProxy(private val config: SConfiguration,
                      private val printer: S.Companion) : LogPrinter {
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

    override fun i(message: Any?) {
        printer.i(message, config)
    }

    override fun d(message: Any?) {
        printer.d(message, config)
    }

    override fun e(message: Any?) {
        printer.e(message, config)
    }

    override fun json(message: String?) {
        printer.json(message, config)
    }
}