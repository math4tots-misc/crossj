package cjx.cordova

import cjx.cordova.Cordova

trait CordovaApp {
    def main() {
        Cordova.onDeviceReady(() -> {
            Self.onDeviceReady()
        })
    }

    def onDeviceReady()
}
