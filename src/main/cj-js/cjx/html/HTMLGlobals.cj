package cjx.html


import cjx.html.Document
import cjx.html.Window
import cjx.JSObject


native class HTMLGlobals {
    def document(): Document
    def window(): Window
}
