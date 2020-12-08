package cjx.cordova.apps.sample

import cjx.JSObject
import cjx.cordova.Cordova
import cjx.cordova.CordovaApp

class Main : CordovaApp {
    def onDeviceReady() {
        IO.println("Hello cordova")
        IO.println("DEVICE READY")

        Cordova.onKeyDown(event -> {
            IO.println("keydown: " + event.key())
        })

        val doc = Cordova.document()

        val canvas = doc.method("getElementById", [JSObject.from("canvas")])
        canvas.setField("width", canvas.field("clientWidth"))
        canvas.setField("height", canvas.field("clientHeight"))

        val width = canvas.field("width").asDouble()
        val height = canvas.field("height").asDouble()
        val ctx = canvas.method("getContext", [JSObject.from("2d")])
        ctx.setField("fillStyle", "blue")
        ctx.method("fillRect", [
            JSObject.from(0),
            JSObject.from(0),
            JSObject.from(width),
            JSObject.from(height),
        ])
        IO.println("width = " + width + ", height = " + height)
    }
}
