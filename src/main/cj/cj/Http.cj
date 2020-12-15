package cj

##
## A basic HTTP request API
##
native class Http {

    ##
    ## Send a GET request and return the contents of the request
    ## as a string.
    ##
    def read(path: String): Promise[String]
}
