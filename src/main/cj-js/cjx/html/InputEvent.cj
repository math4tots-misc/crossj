package cjx.html

import cjx.cordova.Cordova
import cjx.JSObject
import cjx.html.UIEvent

class InputEvent: UIEvent {
    val _obj: JSObject

    def getClass(): JSObject {
        return Cordova.window().field("InputEvent")
    }

    def obj(self: Self): JSObject {
        return self._obj
    }
}
