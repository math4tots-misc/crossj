package cjx.canvas

import cjx.html.CanvasRenderingContext2D
import cjx.html.Document
import cjx.html.HTMLCanvasElement
import cjx.html.MouseEvent
import cjx.html.KeyboardEvent
import cjx.html.Window

trait Canvas2DApp {
    def create(canvas: HTMLCanvasElement): Self
    def canvas(self: Self): HTMLCanvasElement
    def ctx(self: Self): CanvasRenderingContext2D

    def main() {
        val canvas: HTMLCanvasElement = Document.get().getElementById("canvas").cast()
        val self = Self.create(canvas)
        canvas.onResize(event -> self.onResize(event))
        canvas.onClick(event -> self.onClick(event))
        Window.get().requestAnimationFrame(dt -> self._handleFrame(dt))
    }

    def width(self: Self): Int {
        return self.canvas().width()
    }

    def height(self: Self): Int {
        return self.canvas().height()
    }

    def _handleFrame(self: Self, dt: Double) {
        self.tick(dt)
        self.draw()
        Window.get().requestAnimationFrame(dt_ -> self._handleFrame(dt_ / 1000))
    }

    def tick(self: Self, dt: Double)

    def draw(self: Self)

    def onResize(self: Self, _: Unit) {
    }

    def onClick(self: Self, event: MouseEvent)
    def onKeyPress(self: Self, event: MouseEvent)
    def onKeyDown(self: Self, event: MouseEvent)
    def onKeyUp(self: Self, event: MouseEvent)
}
