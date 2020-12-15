package cjx.html

import cjx.html.Storage
import cjx.JSObject
import cjx.JSWrapper
import cjx.cordova.Cordova
import cjx.html.HTMLGlobals


class Window : JSWrapper {
    val _obj: JSObject

    def get(): Window {
        return HTMLGlobals.window()
    }

    def getClass(): JSObject {
        return Self.get().field("Window")
    }

    def obj(self: Self): JSObject {
        return self._obj
    }

    def field(self: Self, name: String): JSObject {
        return self._obj.field(name)
    }

    def open(self: Self, url: String) {
        self._obj.method("open", [JSObject.from(url)])
    }

    def redirect(self: Self, url: String) {
        self.field("location").setField("href", url)
    }

    def localStorage(self: Self): Storage {
        return self._obj.field("localStorage").cast()
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
