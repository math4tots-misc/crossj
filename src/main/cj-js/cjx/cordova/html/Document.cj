package cjx.cordova.html

import cjx.JSObject
import cjx.JSWrapper
import cjx.cordova.Cordova


class Document : JSWrapper {
    val _obj: JSObject

    def getClass(): JSObject {
        return Cordova.window().field("Document")
    }

    def obj(self: Self): JSObject {
        return self._obj
    }

    def getElementById[E: JSWrapper](self: Self, id: String): E {
        return self._obj.method(
            "getElementById",
            [JSObject.from(id)],
        ).cast()
    }
}
