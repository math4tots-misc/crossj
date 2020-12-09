package cjx.cordova.html


import cjx.JSObject
import cjx.JSWrapper
import cjx.cordova.Cordova

class CanvasRenderingContext2D : JSWrapper {
    val _obj: JSObject

    def getClass(): JSObject {
        return Cordova.window().field("CanvasRenderingContext2D")
    }

    def obj(self: Self): JSObject {
        return self._obj
    }

    def setFont(self: Self, font: String) {
        self._obj.setField("font", font)
    }

    def setFillStyle(self: Self, style: String) {
        self._obj.setField("fillStyle", style)
    }

    def setStrokeStyle(self: Self, style: String) {
        self._obj.setField("strokeStyle", style)
    }

    def setLineWidth(self: Self, width: Int) {
        self._obj.setField("lineWidth", width)
    }

    def clearRect(self: Self, x: Int, y: Int, width: Int, height: Int) {
        self._obj.method("clearRect", [
            JSObject.from(x),
            JSObject.from(y),
            JSObject.from(width),
            JSObject.from(height),
        ])
    }

    def fillRect(self: Self, x: Int, y: Int, width: Int, height: Int) {
        self._obj.method("fillRect", [
            JSObject.from(x),
            JSObject.from(y),
            JSObject.from(width),
            JSObject.from(height),
        ])
    }

    def strokeRect(self: Self, x: Int, y: Int, width: Int, height: Int) {
        self._obj.method("strokeRect", [
            JSObject.from(x),
            JSObject.from(y),
            JSObject.from(width),
            JSObject.from(height),
        ])
    }

    def fillText(self: Self, text: String, x: Int, y: Int) {
        self._obj.method("fillText", [
            JSObject.from(text),
            JSObject.from(x),
            JSObject.from(y),
        ])
    }

    def beginPath(self: Self) {
        self._obj.method("beginPath", [])
    }

    def closePath(self: Self) {
        self._obj.method("closePath", [])
    }

    def moveTo(self: Self, x: Int, y: Int) {
        self._obj.method("moveTo", [
            JSObject.from(x),
            JSObject.from(y),
        ])
    }

    def lineTo(self: Self, x: Int, y: Int) {
        self._obj.method("lineTo", [
            JSObject.from(x),
            JSObject.from(y),
        ])
    }

    ## strokes (outlines) the current path with the current stroke style
    def stroke(self: Self) {
        self._obj.method("stroke", [])
    }

    ## fills the current path with the current fill style
    def fill(self: Self) {
        self._obj.method("fill", [])
    }
}
