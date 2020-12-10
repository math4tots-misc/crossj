package cjx.apps.cvsample


import cjx.html.Window
import cjx.html.Document
import cjx.html.HTMLCanvasElement
import cjx.html.CanvasRenderingContext2D


class Main {

    val _canvas: HTMLCanvasElement
    val _ctx: CanvasRenderingContext2D

    def main() {
        IO.println("ASDF")
        val document = Document.get()

        val canvas: HTMLCanvasElement = document.getElementById("canvas").cast()
        val ctx = canvas.getContext2D()

        val self = Self.new(canvas, ctx)

        val width = canvas.width()
        val height = canvas.height()
        ctx.setFillStyle("green")
        ctx.fillRect(0, 0, width // 2, height // 2)
    }

    def width(self: Self): Int {
        return self._canvas.width()
    }

    def height(self: Self): Int {
        return self._canvas.height()
    }

    def onStart(self: Self) {
    }
}
