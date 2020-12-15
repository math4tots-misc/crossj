package cjx.html

import cjx.html.DOMTokenList
import cjx.JSWrapper
import cjx.html.MouseEvent
import cjx.html.KeyboardEvent

trait HTMLElement: JSWrapper {

    def onLoad(self: Self, f: Fn[Unit, Unit]) {
        self.obj().setField("onload", f)
    }

    def onResize(self: Self, f: Fn[Unit, Unit]) {
        self.obj().setField("onresize", f)
    }

    def onKeyDown(self: Self, f: Fn[Unit, KeyboardEvent]) {
        self.obj().setField("onkeydown", f)
    }

    def onClick(self: Self, f: Fn[Unit, MouseEvent]) {
        self.obj().setField("onclick", f)
    }

    def onMouseDown(self: Self, f: Fn[Unit, MouseEvent]) {
        self.obj().setField("onmousedown", f)
    }

    def onMouseUp(self: Self, f: Fn[Unit, MouseEvent]) {
        self.obj().setField("onmouseup", f)
    }

    def onMouseMove(self: Self, f: Fn[Unit, MouseEvent]) {
        self.obj().setField("onmousemove", f)
    }

    def id(self: Self): String {
        return self.obj().field("id").asStringUnsafe()
    }

    def innerText(self: Self): String {
        return self.obj().field("innerText").asStringUnsafe()
    }

    def setInnerText(self: Self, text: String) {
        self.obj().setField("innerText", text)
    }

    def classList(self: Self): DOMTokenList {
        return self.obj().field("classList").cast()
    }
}
