package io.github.heartinfei.slogger.plan

import android.util.Log
import kotlin.math.min

/**
 * Echo log to console.
 * @author Rango on 2019-05-29 249346528@qq.com
 */
class DebugPlan : BasePlan() {

    init {
        addStackIgnoreFilter(DebugPlan::class.java.name)
    }

    override val mTag: String?
        get() = super.mTag ?: Throwable().stackTrace
                .first { it.className !in stackIgnoreFilter }
                .let(::createStackElementTag)

    /**
     * Extract the mTag which should be used for the message from the `element`. By default
     * this will use the class name without any anonymous class suffixes (e.g., `Foo$1`
     * becomes `Foo`).
     *
     * Note: This will not be called if a [manual mTag][.mTag] was specified.
     */
    private fun createStackElementTag(element: StackTraceElement): String? {
        return "(${element.fileName}:${element.lineNumber})"
    }

    /**
     * Break up `message` into maximum-length chunks (if needed) and send to either
     * [Log.println()][Log.println] or
     * [Log.wtf()][Log.wtf] for logging.
     *
     * {@inheritDoc}
     */
    override fun echoLog(priority: Int, tag: String?, message: String) {
        if (message.length < MAX_LOG_LENGTH) {
            if (priority == Log.ASSERT) {
                Log.wtf(tag, message)
            } else {
                Log.println(priority, tag, message)
            }
            return
        }

        // Split by line, then ensure each line can fit into Log's maximum length.
        var i = 0
        val length = message.length
        while (i < length) {
            val chunkEndIndex = min(length,i+ MAX_LOG_LENGTH)
            val chunk = message.substring(i, chunkEndIndex)
            if (priority == Log.ASSERT) {
                Log.wtf(tag, chunk)
            } else {
                Log.println(priority, tag, chunk)
            }
            i = chunkEndIndex
        }
    }
}