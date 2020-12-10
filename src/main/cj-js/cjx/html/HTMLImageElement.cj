package cjx.html

import cjx.JSObject
import cjx.cordova.Cordova
import cjx.html.CanvasImageSource
import cjx.html.HTMLElement

class HTMLImageElement: HTMLElement, CanvasImageSource {
    val _obj: JSObject

    def getClass(): JSObject {
        return Cordova.window().field("HTMLImageElement")
    }

    def obj(self: Self): JSObject {
        return self._obj
    }

    ## Loads data from a url into an HTMLImageElement and returns a promise
    ## with this element that resolves when the image has finished loading.
    def load(url: String): Promise[HTMLImageElement] {
        val img: HTMLImageElement = Cordova.document().createElement("img").cast()
        img.setSrc(url)
        return Promise.create((resolve, reject) -> {
            img.onLoad(() -> resolve.call(img))
            img.onError(() -> reject.call(Error.fromMessage("Failed to load img: " + url)))
        })
    }

    ## height in CSS pixels of the ImageData
    def width(self: Self): Int {
        return self._obj.field("width").asIntUnsafe()
    }

    ## width in CSS pixels of the ImageData
    def height(self: Self): Int {
        return self._obj.field("height").asIntUnsafe()
    }

    def setSrc(self: Self, url: String) {
        self._obj.setField("src", url)
    }

    def onLoad(self: Self, f: Fn[Unit]) {
        self._obj.setField("onload", f)
    }

    def onError(self: Self, f: Fn[Unit]) {
        self._obj.setField("onerror", f)
    }
}
