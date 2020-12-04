package cj


## Ring with multiplicative identity
##
## A trait containing a collection of methods
## and a promise that the methods behave as you would
## expect from a ring.
##
trait Ring {
    def one(): Self
    def negativeOne(): Self
    def zero(): Self
    def __neg(self: Self): Self
    def __add(self: Self, other: Self): Self
    def __sub(self: Self, other: Self): Self
    def __mul(self: Self, other: Self): Self
}
