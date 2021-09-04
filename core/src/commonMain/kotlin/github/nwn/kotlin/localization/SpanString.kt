package github.nwn.kotlin.localization

import github.nwn.graph.*


class SpanString(internal val tags: List<SpanStringTag>) : LocalizedValue<String> {
    object Parser {
        private const val TAG_START = '{'
        private const val TAG_END = '}'

        //Argument Mapping
        private const val ARGUMENT_MAPPING_START = '['
        private const val ARGUMENT_MAPPING_END = ']'
        private const val ARGUMENT_MAPPING_SEPARATOR = ','
        private const val ARGUMENT_SEPARATOR = ','

        private const val ARGUMENT_DEFAULT_VALUE_SEPARATOR = '='

        private const val ESCAPE = '\\'


        private data class ParserState(
            var escaped: Boolean = false,
            var index: Int = 0,
            val buffers: ArrayDeque<StringBuilder> = ArrayDeque<StringBuilder>()
                .apply {
                    addFirst(StringBuilder())
                },
            val tags: MutableList<SpanStringTag> = ArrayList(),
            val argumentIndexMapping: MutableList<Int> = ArrayList(),
            var exception: Exception? = null
        )

        private val graph = graph<ParserState, String> {
            val terminalNode = terminalNode()
            val rawNode = nodeReference()
            val transitionTagNode = nodeReference()
            val tagNode = nodeReference()
            val argumentNode = nodeReference()
            val argumentMappingNode = nodeReference()
            val argumentNodeDefaultValue = nodeReference()
            val argumentFormatType = nodeReference()
            val argumentFormatStyle = nodeReference()
            node(rawNode) {
                initial()
                phaseStep(terminalNode) { state, _, char ->
                    if (char == TAG_START) {
                        transitionTagNode
                    } else {
                        state.buffers.first().append(char)
                        id
                    }
                }
                exit {
                    val raw = it.buffers.first().toString()
                    if (raw.isNotEmpty()) {
                        it.tags.add(SpanStringTag.Raw(raw))
                    }
                    it.buffers.first().clear()
                }
            }
            node(transitionTagNode) {
                phaseStep(terminalNode, isSafeToEnd = false, incrementIndex = false, onEscaped = { _, _, char ->
                    tagNode
                }) { _, _, char ->
                    if (char.isDigit())
                        argumentNode
                    else
                        tagNode
                }

            }
            node(tagNode) {
                phaseStep(terminalNode, isSafeToEnd = false) { state, _, char ->
                    when (char) {
                        TAG_END -> {
                            val buffer = state.buffers.first()
                            val key = buffer.toString()
                            if (key.isNotEmpty()) {
                                state.tags.add(SpanStringTag.LocalizedEntry(key, state.argumentIndexMapping))
                            }
                            state.buffers.first().clear()
                            rawNode
                        }
                        ARGUMENT_MAPPING_START -> {
                            state.buffers.addFirst(StringBuilder())
                            argumentMappingNode
                        }
                        else -> {
                            state.buffers.first().append(char)
                            id
                        }
                    }
                }
            }
            node(argumentMappingNode) {
                enter { state ->
                    state.argumentIndexMapping.clear()
                }
                phaseStep(
                    terminalNode, isSafeToEnd = false
                ) { state, _, char ->
                    when {
                        char.isWhitespace() -> {
                            argumentMappingNode
                        }
                        char.isDigit() -> {
                            state.buffers.first().append(char)
                            argumentMappingNode
                        }
                        char == ARGUMENT_MAPPING_SEPARATOR -> {
                            val buffer = state.buffers.first()
                            val str = buffer.toString()
                            buffer.clear()
                            val int = str.toIntOrNull() ?: return@phaseStep error(
                                terminalNode,
                                state,
                                "Invalid Argument Index: $str"
                            )
                            state.argumentIndexMapping.add(int)
                            argumentMappingNode
                        }
                        char == ARGUMENT_MAPPING_END -> {
                            val buffer = state.buffers.first()
                            val str = buffer.toString()
                            buffer.clear()
                            state.buffers.removeFirst()
                            val int = str.toIntOrNull() ?: return@phaseStep error(
                                terminalNode,
                                state,
                                "Invalid Argument Index: $str"
                            )
                            state.argumentIndexMapping.add(int)
                            tagNode
                        }
                        else -> {
                            error(terminalNode, state, "Invalid Argument Mapping Character '$char'")
                        }
                    }

                }
            }
            node(argumentFormatType) {
                enter { state ->
                    state.buffers.addFirst(StringBuilder())
                }
                phaseStep(terminalNode, isSafeToEnd = false) { state, input, char ->
                    when (char) {
                        TAG_END -> {
                            val formatType = state.buffers.removeFirst().toString()
                            val defaultValue = state.buffers.removeFirst().toString()
                            val indexBuffer = state.buffers.first()
                            val index = indexBuffer.toString()
                            indexBuffer.clear()
                            argumentTag(state, terminalNode, rawNode, index, defaultValue, formatType)

                        }
                        ARGUMENT_SEPARATOR -> {
                            if (state.buffers.first().isNotEmpty())
                                argumentFormatStyle
                            else
                                error(terminalNode, state, "Format Type Missing")
                        }
                        else -> {
                            state.buffers.first().append(char)
                            id
                        }
                    }
                }
            }
            node(argumentFormatStyle) {
                enter { state ->
                    state.buffers.addFirst(StringBuilder())
                }
                phaseStep(terminalNode, isSafeToEnd = false) { state, input, char ->
                    when (char) {
                        TAG_END -> {
                            val formatStyle = state.buffers.removeFirst().toString()
                            val formatType = state.buffers.removeFirst().toString()
                            val defaultValue = state.buffers.removeFirst().toString()
                            val indexBuffer = state.buffers.first()
                            val index = indexBuffer.toString()
                            indexBuffer.clear()
                            argumentTag(state, terminalNode, rawNode, index, defaultValue, formatType, formatStyle)
                        }
                        else -> {
                            state.buffers.first().append(char)
                            id
                        }
                    }
                }
            }
            node(argumentNodeDefaultValue) {
                enter { state ->
                    state.buffers.addFirst(StringBuilder())
                }
                phaseStep(terminalNode, isSafeToEnd = false) { state, input, char ->
                    when (char) {
                        TAG_END -> {
                            val defaultValueBuffer = state.buffers.removeFirst().toString()
                            val indexBuffer = state.buffers.first()
                            val index = indexBuffer.toString()
                            indexBuffer.clear()
                            argumentTag(state, terminalNode, rawNode, index, defaultValueBuffer)
                            rawNode
                        }
                        ARGUMENT_SEPARATOR -> {
                            if (state.buffers.first().toString().toIntOrNull() != null)
                                argumentFormatType
                            else
                                error(terminalNode, state, "Unable to process index '${state.buffers.first()}")
                        }
                        else -> {
                            state.buffers.first().append(char)
                            id
                        }
                    }
                }
            }
            node(argumentNode) {
                phaseStep(terminalNode, isSafeToEnd = false, onEscaped = { state, _, char ->
                    error(
                        terminalNode,
                        state,
                        "Escape not available when processing argument index"
                    )
                }) { state, input, char ->
                    when {
                        char.isDigit() -> {
                            state.buffers.first().append(char)
                            id
                        }
                        char == TAG_END -> {
                            val buffer = state.buffers.first()
                            val index = buffer.toString()
                            buffer.clear()
                            argumentTag(state, terminalNode, rawNode, index)
                        }
                        char == ARGUMENT_DEFAULT_VALUE_SEPARATOR -> {
                            if (state.buffers.first().toString().toIntOrNull() != null)
                                argumentNodeDefaultValue
                            else
                                error(terminalNode, state, "Unable to process index '${state.buffers.first()}")
                        }
                        char == ARGUMENT_SEPARATOR -> {
                            if (state.buffers.first().toString().toIntOrNull() != null) {
                                state.buffers.addFirst(StringBuilder())
                                argumentFormatType

                            } else
                                error(terminalNode, state, "Unable to process index '${state.buffers.first()}")
                        }
                        else -> {
                            error(terminalNode, state, "Unexpected Character '$char'")
                        }
                    }

                }
            }

        }

        private fun GraphNodeBuilder<ParserState, String>.phaseStep(
            terminalNode: NodeReference,
            isSafeToEnd: Boolean = true,
            incrementIndex: Boolean = true,
            onEscaped: GraphNode<ParserState, String>.(ParserState, String, Char) -> NodeReference = { state, input, char ->
                state.buffers.first().append(char)
                state.escaped = false
                id
            },
            op: GraphNode<ParserState, String>.(ParserState, String, Char) -> NodeReference
        ) {
            step { state, input ->
                if (state.index >= input.length) {
                    return@step if (!isSafeToEnd) {
                        error(terminalNode, state, "Unexpected end.")
                    } else
                        terminalNode

                }

                val char = input[state.index]

                when {
                    state.escaped -> {
                        state.index++
                        return@step onEscaped(state, input, char)
                    }
                    char == ESCAPE -> {
                        state.index++
                        state.escaped = true
                        return@step id
                    }
                    else -> {
                        if (incrementIndex)
                            state.index++
                        op(state, input, char)
                    }
                }
            }

        }

        private fun GraphNode<ParserState, String>.error(
            terminalNode: NodeReference,
            state: ParserState,
            message: String?
        ): NodeReference {
            state.exception = SpanStringParseException(message, state.index - 1)
            return terminalNode

        }

        private fun GraphNode<ParserState, String>.argumentTag(
            state: ParserState,
            terminalNode: NodeReference,
            rawNode: NodeReference,
            indexString: String,
            defaultValueString: String = "",
            formatTypeString: String = "",
            formatStyleString: String = ""
        ): NodeReference {

            val index = indexString.toString().toIntOrNull() ?: return error(
                terminalNode,
                state,
                "Unable to process index '$indexString'"
            )
            state.tags.add(SpanStringTag.Argument(index, defaultValueString, formatTypeString, formatStyleString))
            return rawNode
        }

        fun parse(format: String): SpanString {
            val result = graph.process(format, ParserState())
            if (result.exception != null)
                throw result.exception!!
            return SpanString(result.tags)
        }
    }

    override fun get(locale: Locale, provider: LocaleValueProvider): String {
        val builder = StringBuilder()
        tags.forEach {
            builder.append(it.get(locale, provider))
        }
        return builder.toString()
    }

    override fun get(locale: Locale, provider: LocaleValueProvider, vararg args: Any?): String {
        val builder = StringBuilder()
        tags.forEach {
            builder.append(it.get(locale, provider, args))
        }
        return builder.toString()
    }


}

class SpanStringParseException(message: String?, val index: Int) : Exception("$message (index: $index)")

class SpanStringScope(key: String, provider: LocaleValueProvider) {
    private val configurationScope = LocaleValueProviderConfigurationScope(key, provider)
    fun reference(value: LocaleValueReference<*>, vararg argumentIndices: Int) =
        if (argumentIndices.isEmpty()) {
            "{${value.key}}"
        } else {
            "{${value.key}[${argumentIndices.joinToString(separator = ",")}]}"
        }


    fun argument(value: Int, defaultValue: String = "") = if (defaultValue.isNotEmpty()) {
        "{$value=$defaultValue}"
    } else {
        "{$value}"
    }

    fun arg(value: Int, defaultValue: String = "") = argument(value, defaultValue)
    fun quantity(
        arg: Int,
        other: LocalizedValue<String>,
        zero: LocalizedValue<String>? = null,
        one: LocalizedValue<String>? = null,
        two: LocalizedValue<String>? = null,
        few: LocalizedValue<String>? = null,
        many: LocalizedValue<String>? = null,
    ): String = reference(configurationScope.quantity(other, zero, one, two, few, many),arg)
    fun <T : Any> value(value: T) : String = reference(configurationScope.value(value))
}


sealed class SpanStringTag {
    data class Raw(private val value: String) : SpanStringTag() {
        override fun get(locale: Locale, provider: LocaleValueProvider): String = value

        override fun get(locale: Locale, provider: LocaleValueProvider, vararg args: Any?): String =
            formatString(value, args)

    }

    data class LocalizedEntry(private val key: String, private val argumentIndices: List<Int>) : SpanStringTag() {
        override fun get(locale: Locale, provider: LocaleValueProvider): String {
            return locale[provider.getValue(key)].toString()
        }

        override fun get(locale: Locale, provider: LocaleValueProvider, vararg args: Any?): String {
            return locale.get(provider.getValue(key), args).toString()
        }

    }

    data class Argument(
        private val index: Int,
        private val defaultValue: String = "",
        private val formatType: String = "",
        private val formatStyle: String = ""
    ) : SpanStringTag() {
        override fun get(locale: Locale, provider: LocaleValueProvider): String {
            return defaultValue
        }

        override fun get(locale: Locale, provider: LocaleValueProvider, vararg args: Any?): String {
            return if (index >= 0 && index < args.size) {
                args[index].toString()
            } else
                defaultValue
        }

    }


    abstract fun get(locale: Locale, provider: LocaleValueProvider): String
    abstract fun get(locale: Locale, provider: LocaleValueProvider, vararg args: Any?): String
}


data class V(val t: Int) {
    fun add(a: Int, b: Int) {
        sequenceOf(1).toList()
        var a = arrayOf(1, 2, 3)
        val c = listOf(*a, *a)
    }

    fun c() {
        println(::add.invoke(3, 4))
    }
}
