package cjx.html

import cjx.JSObject
import cjx.JSWrapper
import cjx.cordova.Cordova
import cjx.html.HTMLElement
import cjx.html.Window
import cjx.html.HTMLGlobals


class Document : JSWrapper {
    val _obj: JSObject

    def get(): Document {
        return HTMLGlobals.document()
    }

    def getClass(): JSObject {
        return Window.get().field("Document")
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
