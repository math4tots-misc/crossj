package cj

native class StringBuilder {
    def add[S: ToString](self: Self, s: S): Self
    def build(self: Self): String
}
