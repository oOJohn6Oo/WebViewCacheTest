# Demonstrate how to render WebView content instantly

## Key logic

* Using host + path as Key, and WebView as Value in a map to store and reuse the WebView
* Prevent to reload the url when WebView.url isNotBlank
* Using idleHandler to pre-load WebView

Move to [juejin.cn](https://juejin.cn/post/7543556350745313306) to see the whole blog.

## Performance test
> Release + R8 + Local HTML + My Device

 | Type/Duration | Finish Create(avg) | Start Render(avg) |
 | :---: | :---: | :---: |
 | No Cache first Time| 104ms | 230ms |
 | No Cache later Time| 3ms | 64ms |
 | Cached | 0ms | 2ms |



## Result

![no cache vs cached WebView render](https://i.imgur.com/ZEivLwz.png)
