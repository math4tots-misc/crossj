package cjx.html

import cjx.cordova.Cordova
import cjx.html.HTMLElement
import cjx.JSObject

class HTMLDivElement: HTMLElement {
    val _obj: JSObject

    def getClass(): JSObject {
        return Cordova.window().field("HTMLParagraphElement")
    }

    def obj(self: Self): JSObject {
        return self._obj
    }
}
