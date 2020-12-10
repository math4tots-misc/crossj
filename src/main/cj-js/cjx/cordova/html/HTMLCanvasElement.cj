package cjx.cordova.html


import cjx.JSObject
import cjx.cordova.Cordova
import cjx.cordova.html.CanvasRenderingContext2D
import cjx.cordova.html.HTMLElement

class HTMLCanvasElement : HTMLElement {
    val _obj: JSObject

    def getClass(): JSObject {
        return Cordova.window().field("HTMLCanvasElement")
    }

    def obj(self: Self): JSObject {
        return self._obj
    }

    def width(self: Self): Int {
        return self._obj.field("width").asIntUnsafe()
    }

    def setWidth(self: Self, width: Int) {
        self._obj.setField("width", width)
    }

    def height(self: Self): Int {
        return self._obj.field("height").asIntUnsafe()
    }

    def setHeight(self: Self, height: Int) {
        self._obj.setField("height", height)
    }

    def clientWidth(self: Self): Int {
        return self._obj.field("clientWidth").asIntUnsafe()
    }

    def clientHeight(self: Self): Int {
        return self._obj.field("clientHeight").asIntUnsafe()
    }

    def getContext2D(self: Self): CanvasRenderingContext2D {
        return CanvasRenderingContext2D.new(
            self._obj.method("getContext", [JSObject.from("2d")])
        )
    }
}
