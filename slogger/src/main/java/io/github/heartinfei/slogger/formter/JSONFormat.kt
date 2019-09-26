package io.github.heartinfei.slogger.formter

import io.github.heartinfei.slogger.extensions.selfRepeat

/**
 *
 * @author Rango on 2019-09-07 wangqiang@smzdm.com
 */
class JSONFormat : ContentFormat {
    companion object {
        private val LEFT_BLOCK = setOf('{', '[')
        private val RIGHT_BLOCK = setOf('}', ']')
        private const val EMPTY_CHAR = ' '
        private const val BREAK_CHAR = "\n"
        private const val BUFFER_SIZE = 1024 * 4
        private val formatResult = StringBuilder(BUFFER_SIZE)
        private val holderString = StringBuilder(36)
    }

    override fun formatString(content: String?): String {
        if (content.isNullOrEmpty()) {
            return ""
        }
        //记录当前行缩进字符数
        var spaceCount = 0
        //记录当前行缩进字符串
        formatResult.apply {
            clear()
            EMPTY_CHAR + BREAK_CHAR
        }
        holderString.clear()
        content.trimIndent().forEach {
            when {
                LEFT_BLOCK.contains(it) -> {
                    holderString.append(EMPTY_CHAR)
                    formatResult.apply {
                        append(it)
                        append(BREAK_CHAR)
                        append(holderString)
                    }
                }
                RIGHT_BLOCK.contains(it) -> {
                    holderString.delete(0,1)
                    formatResult.apply {
                        append(BREAK_CHAR)
                        append(holderString)
                        append(it)
                    }
                }
                ',' == it ->{
                    formatResult.apply {
                        append(it)
                        append(BREAK_CHAR)
                        append(holderString)
                    }
                }
                else -> formatResult.append(it)
            }
        }
        return formatResult.toString()
    }
}