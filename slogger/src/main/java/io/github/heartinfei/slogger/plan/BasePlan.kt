package io.github.heartinfei.slogger.plan

import android.os.Build
import android.util.Log
import io.github.heartinfei.slogger.LogPrinterProxy
import io.github.heartinfei.slogger.S
import io.github.heartinfei.slogger.SConfiguration
import java.util.regex.Pattern
import kotlin.math.max

/**
 * @author Rango on 2019-05-29 249346528@qq.com
 */
abstract class BasePlan {
    protected open val stackIgnoreFilter = mutableListOf<String>(
            S::class.java.name,
            S.Companion::class.java.name,
            BasePlan::class.java.name,
            LogPrinterProxy::class.java.name
    )

    @get:JvmSynthetic
    internal val explicitTag = ThreadLocal<String>()

    internal open val mTag: String?
        @JvmSynthetic
        get() {
            val tag = explicitTag.get()
            if (tag != null) {
                explicitTag.remove()
            }
            return tag
        }

    /**Add a stack filter.*/
    fun addStackIgnoreFilter(filter: String) {
        stackIgnoreFilter.add(filter)
    }

    /**
     * Return true should be logged.
     */
    protected open fun isLoggable(tag: String?, priority: Int): Boolean = true

    /**
     * Write a echoLog message to its destination. Called for all level-specific methods by default.
     *
     * @param priority Log level. See [Log] for constants.
     * @param tag Explicit or inferred mTag. May be `null`.
     * @param message Formatted echoLog message. May be `null`.
     */
    protected abstract fun echoLog(priority: Int, tag: String?, message: String)

    internal fun assembleAndEchoLog(config: SConfiguration?, priority: Int, message: String?, vararg args: Any?) {
        var tag = if (config?.tag?.isNotEmpty() == true) {
            config.tag
        } else {
            mTag
        }
        if (config?.printThreadInfo == true) {
            tag = tag.plus("Thread#${Thread.currentThread().name}")
        }
        // Tag length limit was removed in API 24.
        if (tag!!.length > MAX_TAG_LENGTH && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            tag = tag.substring(0, MAX_TAG_LENGTH)
        }

        if (!isLoggable(tag, priority)) {
            return
        }
        config?.apply {
            val header = assembleContentHeader(this)
            val content = assembleLogBody(header, message, *args)
            echoLog(priority, tag, content)
        }
    }

    private fun getStackInfo(config: SConfiguration): List<StackTraceElement>? {
        return if (config.printTrackInfo) {
            Thread.currentThread().stackTrace
                    .filter { element ->
                        val content = element.className
                        val defaultFilterResult = content !in stackIgnoreFilter
                        val configFilter = config.trackFilter.orEmpty()
                        val configFilterResult = content.contains(configFilter)
                        return@filter defaultFilterResult && configFilterResult
                    }
                    .toList()
                    .takeLast(max(0, config.trackDeep))
                    .reversed()
        } else null
    }

    private fun assembleContentHeader(config: SConfiguration): String {
        val headerBuilder = StringBuffer()
        getStackInfo(config)?.let {
            headerBuilder.append(" \n")
            var offset = ""
            var indicator = ""
            for (element in it) {
                headerBuilder.append("$offset$indicator$element\n")
                indicator = "↳"
                offset += " "
            }
            val len = headerBuilder.length
            if (len > 1 && '\n' == (headerBuilder[len - 1])) {
                headerBuilder.deleteCharAt(headerBuilder.length - 1).append("\n")
            } else {
                headerBuilder.append("\n")
            }
        }
        return headerBuilder.toString()
    }

    /**
     * Get current thread name.
     */
    private fun getThreadInfo(config: SConfiguration): String {
        return if (config.printThreadInfo) Thread.currentThread().name else ""
    }


    private fun assembleLogBody(header: String, msg: String?, vararg args: Any?): String {
        var content = header + msg
        for (arg in args) {
            content += arg
        }
        return content
    }

    companion object {
        const val MAX_LOG_LENGTH = 4000
        const val MAX_TAG_LENGTH = 23
        //Extract default log withTag when withTag is null.
        val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    }

}
