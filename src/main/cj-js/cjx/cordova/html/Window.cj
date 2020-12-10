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

    ##
    ## Schedule a callback to be called on next frame for redraw.
    ##
    ## Like the javascript requestAnimationFrame, except the time is given in
    ## seconds rather than milliseconds.
    ##
    ## https://developer.mozilla.org/en-US/docs/Web/API/window/requestAnimationFrame
    ##
    def requestAnimationFrame(self: Self, f: Fn[Unit, Double]) {
        val f2: Fn[Unit, Double] = dt -> f.call(dt / 1000)
        self._obj.method("requestAnimationFrame", [JSObject.from(f2)])
    }
}
