package cj

# Basic IO API
# Writing/reading from stdin, stdout, stderr
#
# Also, a way to panic and "crash"
#
native class IO {
    def println[T : ToString](t: T)
    def print[T : ToString](t: T)
    def eprintln[T : ToString](t: T)
    def eprint[T : ToString](t: T)
    def panic[T: ToString](t: T) : NoReturn
}
