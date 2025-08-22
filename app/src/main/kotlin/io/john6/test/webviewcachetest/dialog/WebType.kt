package io.john6.test.webviewcachetest.dialog

sealed class WebType(val type: String) {
    data object Js : WebType("Js")
    data object JsWithCache : WebType("JsWithCache")
    data object MessageWithCache : WebType("MessageWithCache")
}

fun String.map2WebType() = when (this) {
    "Js" -> WebType.Js
    "JsWithCache" -> WebType.JsWithCache
    "MessageWithCache" -> WebType.MessageWithCache
    else -> WebType.Js
}