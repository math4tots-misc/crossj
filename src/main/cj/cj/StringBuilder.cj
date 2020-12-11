package cj

native class StringBuilder {
    def add[S: ToString](self: Self, s: S)
    def build(self: Self): String
}
