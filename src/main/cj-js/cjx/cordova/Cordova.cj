package cjx.cordova



native class Cordova {
    def onDeviceReady(f: Fn[Unit])
    def onPause(f: Fn[Unit])
    def onResume(f: Fn[Unit])
}
