package io.github.heartinfei.slogger.formter

import io.github.heartinfei.slogger.extensions.selfRepeat

/**
 *
 * @author Rango on 2019-09-07 wangqiang@smzdm.com
 */
class JSONFormater : ContentFormat {
    private val leftBlock = setOf("{", "[")
    private val rightBlock = setOf("}", "]")

    companion object {
        private const val EMPTY_CHAR = "  "
        private const val BREAK_CHAR = "\n"
    }

    override fun formatString(json: String?): String {
        if (json.isNullOrEmpty()) {
            return ""
        }
        var formatJsonString = EMPTY_CHAR + BREAK_CHAR
        var holderString = ""
        var spaceCount = 0

        json.trimIndent().forEach {
            val w = it.toString()
            if (leftBlock.contains(w)) {
                holderString = EMPTY_CHAR.selfRepeat(++spaceCount)
                formatJsonString += (w + BREAK_CHAR + holderString)
                return@forEach
            }

            if (rightBlock.contains(w)) {
                holderString = EMPTY_CHAR.selfRepeat(--spaceCount)
                formatJsonString += (BREAK_CHAR + holderString + w)
                return@forEach
            }

            if (w == ",") {
                formatJsonString += (w + BREAK_CHAR + holderString)
                return@forEach
            }
            formatJsonString += w
        }
        return formatJsonString
    }
}