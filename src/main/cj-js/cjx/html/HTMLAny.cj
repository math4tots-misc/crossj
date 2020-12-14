package cjx.html

import cjx.cordova.Cordova
import cjx.JSObject
import cjx.html.HTMLElement

##
## An HTMLElement whose exact type is not known
##
class HTMLAny: HTMLElement {
    val _obj: JSObject

    def getClass(): JSObject {
        return Cordova.window().field("HTMLElement")
    }

    def obj(self: Self): JSObject {
        return self._obj
    }

    ## Casts Self to a more specific HTMLElement type
    def cast[T: HTMLElement](self: Self): T {
        return self._obj.cast()
    }
}
