package github.nwn.kotlin.localization

import kotlin.test.*

class SpanStringTests {
    companion object {
        private val EMPTY_RAW = SpanStringTag.Raw("")
    }
    @Test
    fun Raw() {
        val span = SpanString.Parser.parse("raw string")
        assertTrue {
            span.tags == listOf(SpanStringTag.Raw("raw string"))
        }
    }
    @Test
    fun `Entry Reference`() {
        val span = SpanString.Parser.parse("raw string {locale.moose}")
        assertEquals(span.tags,listOf(SpanStringTag.Raw("raw string "),SpanStringTag.LocalizedEntry("locale.moose", emptyList())))
        assertTrue(!span.tags.contains(EMPTY_RAW))
    }
    @Test
    fun `Multiple Entry Reference`() {
        val span = SpanString.Parser.parse("raw string {locale.moose}{locale.cow} {locale.sheep}")
        assertEquals(span.tags,listOf(SpanStringTag.Raw("raw string "),SpanStringTag.LocalizedEntry("locale.moose", emptyList()),SpanStringTag.LocalizedEntry("locale.cow", emptyList()),SpanStringTag.Raw(" "),SpanStringTag.LocalizedEntry("locale.sheep", emptyList()) ))
        assertTrue(!span.tags.contains(EMPTY_RAW))
    }
    @Test
    fun `Entry Reference String No Empty Raw`() {
        val span = SpanString.Parser.parse("raw string {locale.moose}")
        assertEquals(span.tags,listOf(SpanStringTag.Raw("raw string "),SpanStringTag.LocalizedEntry("locale.moose", emptyList())))
        assertTrue(!span.tags.contains(EMPTY_RAW))
    }
    @Test
    fun `Entry Reference Argument Remapping`() {
        val span = SpanString.Parser.parse("raw string {locale.moose[1,3,4]}")
        assertEquals(span.tags,listOf(SpanStringTag.Raw("raw string "),SpanStringTag.LocalizedEntry("locale.moose", listOf(1,3,4))))
        assertTrue(!span.tags.contains(EMPTY_RAW))
    }
    @Test
    fun `Entry Reference Invalid Argument Character`() {
        assertFailsWith<SpanStringParseException> {
                SpanString.Parser.parse("raw string {locale.moose[1,3,f]}")
        }
    }
    @Test
    fun `Entry Reference Argument Mapping Unexpected End`() {
        assertFailsWith<SpanStringParseException> {
            SpanString.Parser.parse("raw string {locale.moose[1,3,")
        }
        assertFailsWith<SpanStringParseException> {
            SpanString.Parser.parse("raw string {locale.moose[1,3")
        }
    }
    @Test
    fun `Entry Reference Tag Unexpected End`() {
        assertFailsWith<SpanStringParseException> {
            SpanString.Parser.parse("raw string {locale.moose")
        }


    }
    @Test
    fun `Argument Tag`() {
        assertTrue {
            SpanString.Parser.parse("raw string {0} {1}").tags == listOf(SpanStringTag.Raw("raw string "),SpanStringTag.Argument(0),SpanStringTag.Raw(" "),SpanStringTag.Argument(1))
        }
    }
    @Test
    fun `Argument Tag Default Value`() {
        assertTrue {
            SpanString.Parser.parse("raw string {0=cow} {1}").tags.apply { println(this) } == listOf(SpanStringTag.Raw("raw string "),SpanStringTag.Argument(0,"cow"),SpanStringTag.Raw(" "),SpanStringTag.Argument(1))
        }
    }
    @Test
    fun `Argument Tag Format Type`() {
        assertTrue {
            SpanString.Parser.parse("raw string {0=cow} {1,currency}").tags.apply { println(this) } == listOf(SpanStringTag.Raw("raw string "),SpanStringTag.Argument(0,"cow"),SpanStringTag.Raw(" "),SpanStringTag.Argument(1,formatType = "currency"))
        }
    }
    @Test
    fun `Argument Tag Format Style`() {
        assertTrue {
            SpanString.Parser.parse("raw string {0=cow} {1,currency,$#.##}").tags.apply { println(this) } == listOf(SpanStringTag.Raw("raw string "),SpanStringTag.Argument(0,"cow"),SpanStringTag.Raw(" "),SpanStringTag.Argument(1,formatType = "currency",formatStyle = "\$#.##"))
        }
    }
}