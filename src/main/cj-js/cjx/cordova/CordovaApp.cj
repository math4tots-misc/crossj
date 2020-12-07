package cjx.cordova

import cjx.cordova.Cordova

trait CordovaApp {
    def main() {
        Cordova.onDeviceReady(() -> {
            Cordova.onPause(() -> Self.onPause())
            Cordova.onResume(() -> Self.onResume())
            Self.onDeviceReady()
        })
    }

    def onDeviceReady()

    def onPause() {
    }

    def onResume() {
    }
}
