package cjx.apps.cvsample


import cjx.html.Window
import cjx.html.Document
import cjx.html.HTMLCanvasElement


class Main {
    def main() {
        IO.println("ASDF")
        val document = Document.get()

        val canvas: HTMLCanvasElement = document.getElementById("canvas").cast()
        val ctx = canvas.getContext2D()
        val width = canvas.width()
        val height = canvas.height()
        ctx.setFillStyle("green")
        ctx.fillRect(0, 0, width // 2, height // 2)
    }
}
