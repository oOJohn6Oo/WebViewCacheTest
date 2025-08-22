# Demonstrate how to render WebView content instantly

## Key logic

* Using host + path as Key, and WebView as Value in a map to store and reuse the WebView
* Prevent to reload the url when WebView.url isNotBlank
* Using idleHandler to pre-load WebView




## Result

![no cache vs cached WebView render](https://i.imgur.com/ZEivLwz.png)