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

        Cordova.onResize(event -> {
            IO.println("resized: (" + Cordova.width() + ", " + Cordova.height() + ")")
        })

        Cordova.onClick(event -> {
            IO.println("click: " + event.x() + ", " + event.y())
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
        IO.println("canvas.width = " + width + ", canvas.height = " + height)
        IO.println(
            "body.width = " +
            Cordova.width() +
            ", body.height = " +
            Cordova.height()
        )
    }
}
