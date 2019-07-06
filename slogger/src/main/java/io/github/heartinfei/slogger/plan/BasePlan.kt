package io.github.heartinfei.slogger.plan

import android.util.Log
import io.github.heartinfei.slogger.Configuration
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.reflect.KClass

/**
 * @author Rango on 2019-05-29 249346528@qq.com
 */
abstract class BasePlan {
    private val dateFormater = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    protected open val stackIgnoreFilter = mutableListOf<String>()

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
    fun addStackIgnoreFilter(clz: KClass<Any>) {
        stackIgnoreFilter.add(clz.java.name)
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

    internal fun assembleAndEchoLog(config: Configuration?, priority: Int, message: String?, vararg args: Any?) {
        val tag = if (config?.tag?.isNotEmpty() == true) {
            config.tag
        } else {
            mTag
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

    private fun getStackInfo(filter: String?, trackDeep: Int): List<StackTraceElement> {
        return Thread.currentThread().stackTrace
                .filter { element ->
                    val name = element.className
                    val libFilter = name !in stackIgnoreFilter
                    val pFilter: Boolean = filter?.let { f ->
                        name.contains(f)
                    } ?: libFilter
                    return@filter libFilter && pFilter
                }
                .toList()
                .takeLast(max(0, trackDeep))
                .reversed()
    }


    private fun assembleContentHeader(config: Configuration): String {
        var offset = ""
        var indicator = ""
        val header = StringBuilder()
        if (config.printThreadInfo) {
            header.append(getThreadInfo())
        }
        if (config.printTimeStamp) {
            header.append("#" + getTimeStr())
        }
        val stackInfo = if (config.printTrackInfo) {
            getStackInfo(config.trackFilter, config.trackDeep)
        } else null

        stackInfo?.takeIf {
            it.isNotEmpty()
        }?.apply {
            header.append("#->\n")
            for (element in this) {
                header.append("$offset$indicator$element\n")
                indicator = "↳"
                offset += " "
            }
            val len = header.length
            if (len > 1 && '\n' == (header[len - 1])) {
                header.deleteCharAt(header.length - 1).append("\n")
            } else {
                header.append("\n")
            }
        }
        return header.toString()
    }

    private fun getTimeStr(): String {
        return dateFormater.format(Date())
    }

    /**
     * Get current thread name.
     */
    private fun getThreadInfo(): String = "Thread#${Thread.currentThread().name}"


    private fun assembleLogBody(header: String, msg: String?, vararg args: Any?): String {
        var content = header + msg
        for (arg in args) {
            content += arg
        }
        return content
    }

}
