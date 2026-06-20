package com.imdumb.app.presentation.common

import android.os.Build
import android.text.Html
import android.text.Spanned

object HtmlFormatter {

    @Suppress("DEPRECATION")
    fun fromHtml(html: String): Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(html)
    }
}
