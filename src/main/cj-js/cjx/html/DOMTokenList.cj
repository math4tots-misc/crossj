package cjx.html

import cjx.cordova.Cordova
import cjx.JSObject
import cjx.JSWrapper

##
##
## Methods that modify the DOMTokenList automatically trim any
## excess whitespace and remove duplicate values from the list.
##
class DOMTokenList: JSWrapper {
    val _obj: JSObject

    def getClass(): JSObject {
        return Cordova.window().field("DOMTokenList")
    }

    def obj(self: Self): JSObject {
        return self._obj
    }

    def __contains(self: Self, token: String): Bool {
        return self._obj.method("contains", [JSObject.from(token)]).asBoolUnsafe()
    }

    def add(self: Self, token: String) {
        self._obj.method("add", [JSObject.from(token)])
    }

    def remove(self: Self, token: String) {
        self._obj.method("remove", [JSObject.from(token)])
    }

    def toggle(self: Self, token: String) {
        self._obj.method("toggle", [JSObject.from(token)])
    }
}
