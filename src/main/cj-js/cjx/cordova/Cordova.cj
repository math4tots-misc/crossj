package cjx.cordova

import cjx.JSObject
import cjx.cordova.html.KeyboardEvent
import cjx.cordova.html.UIEvent

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

    def onResize(f: Fn[Unit, UIEvent])

    def onKeyDown(f: Fn[Unit, KeyboardEvent])
    def onKeyUp(f: Fn[Unit, KeyboardEvent])
}
