package cjx.html

import cjx.html.Window
import cjx.JSObject
import cjx.html.HTMLElement

class HTMLInputElement: HTMLElement {
    val _obj: JSObject

    def getClass(): JSObject {
        return Window.get().field("HTMLInputElement")
    }

    def obj(self: Self): JSObject {
        return self._obj
    }

    def value(self: Self): String {
        return self._obj.field("value").asString()
    }

    def setValue(self: Self, value: String) {
        self._obj.setField("value", value)
    }
}
