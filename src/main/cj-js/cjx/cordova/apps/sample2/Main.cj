package cjx.cordova.apps.sample2

import cjx.JSObject
import cjx.cordova.Cordova
import cjx.cordova.CordovaApp
import cjx.cordova.html.HTMLCanvasElement
import cjx.cordova.html.HTMLImageElement
import cjx.cordova.html.CanvasRenderingContext2D
import cjx.cordova.html.Window
import cjx.cordova.html.MouseEvent

class Main: CordovaApp {

    val _canvas: HTMLCanvasElement
    val _ctx: CanvasRenderingContext2D
    var _x: Int
    var _y: Int

    def onDeviceReady() {
        val canvas: HTMLCanvasElement = Cordova.document().getElementById("canvas").cast()
        canvas.setWidth(canvas.clientWidth())
        canvas.setHeight(canvas.clientHeight())
        val width = canvas.width()
        val height = canvas.height()
        val ctx = canvas.getContext2D()

        val self = Main.new(canvas, ctx, 20, 20)
        Cordova.onClick(event -> self.onClick(event))
        Cordova.window().requestAnimationFrame(dt -> self.tick(dt))
    }

    def tick(self: Self, dt: Double) {
        val canvas = self._canvas
        val ctx = self._ctx
        val width = canvas.width()
        val height = canvas.height()
        val x = self._x
        val y = self._y

        ctx.setFillStyle("#0F0F00")
        ctx.fillRect(0, 0, width, height)

        ctx.setFillStyle("#1F2F5F")
        ctx.fillRect(x - 20, y - 20, 40, 40)

        Cordova.window().requestAnimationFrame(dt_ -> self.tick(dt_))
    }

    def onClick(self: Self, event: MouseEvent) {
        self._x = event.x().toInt()
        self._y = event.y().toInt()
    }
}
