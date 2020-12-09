package cjx.cordova.apps.sample

import cjx.JSObject
import cjx.cordova.Cordova
import cjx.cordova.CordovaApp
import cjx.cordova.html.HTMLCanvasElement
import cjx.cordova.html.CanvasRenderingContext2D

class Main : CordovaApp {
    def onDeviceReady() {
        IO.println("Hello cordova")
        IO.println("DEVICE READY")

        val doc = Cordova.document()

        val canvas: HTMLCanvasElement = doc.getElementById("canvas")
        canvas.setWidth(canvas.clientWidth())
        canvas.setHeight(canvas.clientHeight())
        val width = canvas.width()
        val height = canvas.height()
        val ctx = canvas.getContext2D()

        Cordova.onKeyDown(event -> {
            IO.println("keydown: " + event.key())
        })

        Cordova.onResize(event -> {
            canvas.setWidth(canvas.clientWidth())
            canvas.setHeight(canvas.clientHeight())
            Self.redraw(canvas, ctx, 150, 150)
            IO.println("resized: (" + Cordova.width() + ", " + Cordova.height() + ")")
        })

        Cordova.onClick(event -> {
            IO.println("click: " + event.x() + ", " + event.y())
            Self.redraw(canvas, ctx, event.x().toInt(), event.y().toInt())
        })

        ctx.setFillStyle("blue")
        ctx.fillRect(0, 0, width, height)
        IO.println("canvas.width = " + width + ", canvas.height = " + height)
        IO.println(
            "body.width = " +
            Cordova.width() +
            ", body.height = " +
            Cordova.height()
        )

        ctx.setFillStyle("black")
        ctx.setLineWidth(10)
        ctx.strokeRect(75, 140, 150, 110)
        ctx.fillRect(130, 190, 40, 60)
        ctx.beginPath()
        ctx.moveTo(50, 140)
        ctx.lineTo(150, 60)
        ctx.lineTo(250, 140)
        ctx.closePath()
        ctx.stroke()
    }

    def redraw(canvas: HTMLCanvasElement, ctx: CanvasRenderingContext2D, x: Int, y: Int) {
        ctx.setFillStyle("blue")
        ctx.fillRect(0, 0, canvas.width(), canvas.height())
        val ux = x - 150
        val uy = y - 150
        ctx.setFillStyle("black")
        ctx.setLineWidth(10)
        ctx.strokeRect(ux + 75, uy + 140, 150, 110)
        ctx.fillRect(ux + 130, uy + 190, 40, 60)
        ctx.beginPath()
        ctx.moveTo(ux + 50, uy + 140)
        ctx.lineTo(ux + 150, uy + 60)
        ctx.lineTo(ux + 250, uy + 140)
        ctx.closePath()
        ctx.stroke()
    }
}
