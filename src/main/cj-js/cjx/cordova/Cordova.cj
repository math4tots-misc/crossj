package cjx.cordova

import cjx.JSObject
import cjx.cordova.html.KeyboardEvent
import cjx.cordova.html.UIEvent
import cjx.cordova.html.MouseEvent

native class Cordova {

    ## Retrieves the 'document' global variable in a browser context.
    def document(): JSObject

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
