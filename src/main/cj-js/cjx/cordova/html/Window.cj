package cjx.cordova.html

import cjx.JSObject
import cjx.JSWrapper
import cjx.cordova.Cordova


class Window : JSWrapper {
    val _obj: JSObject

    def getClass(): JSObject {
        return Cordova.window().field("Window")
    }

    def obj(self: Self): JSObject {
        return self._obj
    }

    def field(self: Self, name: String): JSObject {
        return self._obj.field(name)
    }
}
