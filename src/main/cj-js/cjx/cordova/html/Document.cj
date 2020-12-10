package cjx.cordova.html

import cjx.JSObject
import cjx.JSWrapper
import cjx.cordova.Cordova
import cjx.cordova.html.HTMLElement


class Document : JSWrapper {
    val _obj: JSObject

    def getClass(): JSObject {
        return Cordova.window().field("Document")
    }

    def obj(self: Self): JSObject {
        return self._obj
    }

    def getElementById(self: Self, id: String): JSObject {
        return self._obj.method(
            "getElementById",
            [JSObject.from(id)],
        )
    }

    def createElement(self: Self, tagName: String): JSObject {
        return self._obj.method("createElement", [JSObject.from(tagName)])
    }
}
