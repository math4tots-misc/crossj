package cj


## Async IO tools
native class AIO {

    ## Returns a promise the resolves after a given number of seconds
    def delay(nsec: Double): Promise[Unit]
}
