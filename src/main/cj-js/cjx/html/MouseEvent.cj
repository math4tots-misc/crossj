package cjx.html

import cjx.JSObject
import cjx.html.UIEvent


## Native wrapper around the browser "MouseEvent"
native class MouseEvent: UIEvent {

    def getClass(): JSObject
    def obj(self: Self): JSObject

    ## true if the alt key was down when the mouse event was fired
    def altKey(self: Self): Bool

    ## The button number that was pressed, if applicable
    ##
    ## 0: Main button pressed, usually the left button
    ## 1: Auxiliary button pressed, usually the wheel or middle button
    ## 2: Secondary button, usually the right button
    ## 3: Fourth button, typically the Browser Back button
    ## 4: Fifth button, typically the Browser Forward button
    ##
    def button(self: Self): Int

    ## X coordinate of the mouse pointer in local (DOM content) corodinates
    def clientX(self: Self): Double

    ## Y coordinate of the mouse pointer in local (DOM content) corodinates
    def clientY(self: Self): Double

    ## true if the control key was down when the mouse event was fired
    def ctrlKey(self: Self): Bool

    ## true if the meta key was down when the mouse event was fired
    def metaKey(self: Self): Bool

    ## X coordinate of the mouse pointer relative to the position
    ## of the last mousemove event
    def movementX(self: Self): Double

    ## Y coordinate of the mouse pointer relative to the position
    ## of the last mousemove event
    def movementY(self: Self): Double

    ## true if the shift key was down when the mouse event was fired
    def shiftKey(self: Self): Bool

    ## Alias for self.clientX()
    def x(self: Self): Double

    ## Alias for self.clientY()
    def y(self: Self): Double
}
