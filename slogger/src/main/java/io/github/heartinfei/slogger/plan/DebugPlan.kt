package io.github.heartinfei.slogger.plan

import android.os.Build
import android.util.Log
import io.github.heartinfei.slogger.S
import java.util.regex.Pattern

/**
 * Echo log to console.
 * @author Rango on 2019-05-29 249346528@qq.com
 */
class DebugPlan : BasePlan() {
    override val stackIgnoreFilter = mutableListOf(
        S::class.java.name,
        S.Companion::class.java.name,
        BasePlan::class.java.name,
        DebugPlan::class.java.name
    )

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
        var tag = element.className.substringAfterLast('.')
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        // Tag length limit was removed in API 24.
        return if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tag
        } else {
            tag.substring(0, MAX_TAG_LENGTH)
        }
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

    companion object {
        private const val MAX_LOG_LENGTH = 4000
        private const val MAX_TAG_LENGTH = 23
        //Extract default log withTag when withTag is null.
        private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    }
}