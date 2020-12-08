package cjx.cordova

import cjx.JSObject

native class Cordova {

    ## Retrieves the 'document' global variable in a browser context.
    def document(): JSObject

    def onDeviceReady(f: Fn[Unit])
    def onPause(f: Fn[Unit])
    def onResume(f: Fn[Unit])
}
