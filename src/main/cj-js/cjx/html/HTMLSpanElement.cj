package cjx.html

import cjx.cordova.Cordova
import cjx.JSObject
import cjx.html.HTMLElement

class HTMLSpanElement {
    val _obj: JSObject

    def getClass(): JSObject {
        return Cordova.window().field("HTMLParagraphElement")
    }

    def obj(self: Self): JSObject {
        return self._obj
    }
}
