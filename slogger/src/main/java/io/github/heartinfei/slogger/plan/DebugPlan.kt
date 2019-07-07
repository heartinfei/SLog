package io.github.heartinfei.slogger.plan

import android.util.Log

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
            var newline = message.indexOf('\n', i)
            newline = if (newline != -1) newline else length
            do {
                val end = Math.min(newline, i + MAX_LOG_LENGTH)
                val part = message.substring(i, end)
                if (priority == Log.ASSERT) {
                    Log.wtf(tag, part)
                } else {
                    Log.println(priority, tag, part)
                }
                i = end
            } while (i < newline)
            i++
        }
    }
}