package cjx.html

import cjx.html.Window
import cjx.JSWrapper
import cjx.JSObject

##
## https://developer.mozilla.org/en-US/docs/Web/API/Storage
##
class Storage: JSWrapper {
    val _obj: JSObject

    def getClass(): JSObject {
        return Window.get().field("Storage")
    }

    def obj(self: Self): JSObject {
        return self._obj
    }

    def size(self: Self): Int {
        return self._obj.field("length").asIntUnsafe()
    }

    def key(self: Self, i: Int): String {
        return self._obj.method("key", [JSObject.from(i)]).asStringUnsafe()
    }

    def getItem(self: Self, key: String): Nullable[String] {
        return self._obj.method("getItem", [JSObject.from(key)]).asNullableStringUnsafe()
    }

    def setItem(self: Self, key: String, value: String) {
        self._obj.method("setItem", [JSObject.from(key), JSObject.from(value)])
    }

    def removeItem(self: Self, key: String) {
        self._obj.method("removeItem", [JSObject.from(key)])
    }

    def clear(self: Self) {
        self._obj.method("clear", [])
    }
}
