package cjx.cordova

import cjx.JSObject
import cjx.html.Document
import cjx.html.KeyboardEvent
import cjx.html.MouseEvent
import cjx.html.UIEvent
import cjx.html.Window

native class Cordova {

    ## Retrieves the 'document' global variable in a browser context.
    def document(): Document

    ## Retrieves the 'window' global variable in a browser context.
    def window(): Window

    ## Gets the width in pixels of the body element
    def width(): Int

    ## Gets the height in pixels of the body element
    def height(): Int

    def onDeviceReady(f: Fn[Unit])
    def onPause(f: Fn[Unit])
    def onResume(f: Fn[Unit])

    ## Callback called when the window is resized.
    ## You can use Cordova.width()/.height() to get the new dimensions
    ## It accepts a dummy Unit argument which is an unspecialized UIEvent
    ## that doesn't currently seem to contain any useful data.
    def onResize(f: Fn[Unit, Unit])

    def onKeyDown(f: Fn[Unit, KeyboardEvent])
    def onKeyUp(f: Fn[Unit, KeyboardEvent])

    def onClick(f: Fn[Unit, MouseEvent])
    def onMouseDown(f: Fn[Unit, MouseEvent])
    def onMouseUp(f: Fn[Unit, MouseEvent])
}
