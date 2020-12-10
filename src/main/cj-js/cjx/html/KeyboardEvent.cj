package cjx.html


## Native wrapper around the browser "KeyboardEvent"
native class KeyboardEvent {

    ## true if the Alt (or Option on OSX) key was active
    ## when the key was generated
    def altKey(self: Self): Bool

    ## Returns the code value of the physical key represented by the event
    def code(self: Self): String

    ## true if the Ctrl key was active when the key event
    ## was generated
    def ctrlKey(self: Self): Bool

    ## Returns the string value of the key represented by the event
    def key(self: Self): String

    ## true if the Meta key (or Command on OSX or Windows key on Windows)
    ## was active when the key event was generated
    def metaKey(self: Self): Bool

    ## true if the key is being held down such that it is automatically
    ## repeating
    def repeat(self: Self): Bool

    ## true if the Shift key was active when the key event was generated
    def shiftKey(self: Self): Bool
}
