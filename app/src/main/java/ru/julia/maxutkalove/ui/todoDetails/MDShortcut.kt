package ru.julia.maxutkalove.ui.todoDetails

import android.graphics.Typeface
import android.icu.lang.UProperty.INT_START
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.annotation.IdRes
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT


/**
 * This class contains information about button with MD shortcut
 * @param buttonResId is resource id reference for click listener
 * @param name is name of shortcut. Should reflect style of markdown block
 * @param shortcut contains MD block code
 * @param cursorPosition is position of the cursor relative to the markup block
 * @param selectionLength is number of highlighted characters after @code cursorPosition. 0 by default
 * */
abstract class MDShortcut(@IdRes val buttonResId: Int) {
    abstract val name: Spanned
    abstract val shortcut: String
    abstract val cursorPosition: Int
    val selectionLength: Int = 0

    class H1(@IdRes buttonResId: Int) : MDShortcut(buttonResId) {
        override val name: Spanned = SpannableString(HtmlCompat.fromHtml("<h1>H1</h1>", FROM_HTML_MODE_COMPACT))
        override val shortcut: String = "# "
        override val cursorPosition: Int = 2
    }

    class H2(@IdRes buttonResId: Int) : MDShortcut(buttonResId) {
        override val name: Spanned = SpannableString(HtmlCompat.fromHtml("<h2>H2</h2>", FROM_HTML_MODE_COMPACT))
        override val shortcut: String = "## "
        override val cursorPosition: Int = 3
    }

    class H3(@IdRes buttonResId: Int) : MDShortcut(buttonResId) {
        override val name: Spanned = SpannableString(HtmlCompat.fromHtml("<h3>H3</h3>", FROM_HTML_MODE_COMPACT))
        override val shortcut: String = "### "
        override val cursorPosition: Int = 4
    }

    class H4(@IdRes buttonResId: Int) : MDShortcut(buttonResId) {
        override val name: Spanned = SpannableString(HtmlCompat.fromHtml("<h4>H4</h4>", FROM_HTML_MODE_COMPACT))
        override val shortcut: String = "#### "
        override val cursorPosition: Int = 5
    }

    class H5(@IdRes buttonResId: Int) : MDShortcut(buttonResId) {
        override val name: Spanned = SpannableString(HtmlCompat.fromHtml("<h5>H5</h5>", FROM_HTML_MODE_COMPACT))
        override val shortcut: String = "##### "
        override val cursorPosition: Int = 6
    }

    class H6(@IdRes buttonResId: Int) : MDShortcut(buttonResId) {
        override val name: Spanned = SpannableString(HtmlCompat.fromHtml("<h6>H6</h6>", FROM_HTML_MODE_COMPACT))
        override val shortcut: String = "######  "
        override val cursorPosition: Int = 7
    }

    class Bold(@IdRes buttonResId: Int) : MDShortcut(buttonResId) {
        override val name: Spanned = SpannableString("Bold").apply {
            setSpan(StyleSpan(Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        override val shortcut: String = "****"
        override val cursorPosition: Int = 2
    }

    class Italic(@IdRes buttonResId: Int) : MDShortcut(buttonResId) {
        override val name: Spanned = SpannableString("Italic").apply {
            setSpan(StyleSpan(Typeface.ITALIC), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        override val shortcut: String = "__"
        override val cursorPosition: Int = 1
    }

    class ListItem(@IdRes buttonResId: Int) : MDShortcut(buttonResId) {
        override val name: Spanned = SpannableString("⬤")
        override val shortcut: String = "- "
        override val cursorPosition: Int = 2
    }
}
