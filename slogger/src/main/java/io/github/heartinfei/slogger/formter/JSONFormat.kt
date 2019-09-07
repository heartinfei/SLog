package io.github.heartinfei.slogger.formter

import io.github.heartinfei.slogger.extensions.selfRepeat

/**
 *
 * @author Rango on 2019-09-07 wangqiang@smzdm.com
 */
class JSONFormat : ContentFormat {
    companion object {
        private val LEFT_BLOCK = setOf("{", "[")
        private val RIGHT_BLOCK = setOf("}", "]")
        private const val EMPTY_CHAR = "  "
        private const val BREAK_CHAR = "\n"
    }

    override fun formatString(content: String?): String {
        if (content.isNullOrEmpty()) {
            return ""
        }
        //记录当前行缩进字符数
        var spaceCount = 0
        //记录当前行缩进字符串
        var holderString = ""
        var formatResult = EMPTY_CHAR + BREAK_CHAR
        content.trimIndent().forEach {
            val w = it.toString()
            if (LEFT_BLOCK.contains(w)) {
                holderString = EMPTY_CHAR.selfRepeat(++spaceCount)
                formatResult += (w + BREAK_CHAR + holderString)
                return@forEach
            }

            if (RIGHT_BLOCK.contains(w)) {
                holderString = EMPTY_CHAR.selfRepeat(--spaceCount)
                formatResult += (BREAK_CHAR + holderString + w)
                return@forEach
            }

            if (w == ",") {
                formatResult += (w + BREAK_CHAR + holderString)
                return@forEach
            }
            formatResult += w
        }
        return formatResult
    }
}