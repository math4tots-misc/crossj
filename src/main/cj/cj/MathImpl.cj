package cj


## Largely private native functions for Math stuff that's best implemented
## by the underlying platform (e.g. trig functions)
##
## Actually, MathImpl should never actually be used.
## All usage of MathImpl should be in Math, and all methods in Math
## that rely on MathImpl should be intercepted by the SpecialMethods
## that convert those calls to direct calls in the underlying platform.
##
## Any attempt to actually use methods here will result in a runtime
## error.
##
native class MathImpl {
    def e(): Double
    def pi(): Double
    def random(): Double
    def ceil(x: Double): Int
    def floor(x: Double): Int
    def sin(angle: Double): Double
    def cos(angle: Double): Double
    def tan(angle: Double): Double
    def asin(angle: Double): Double
    def acos(angle: Double): Double
    def atan(angle: Double): Double
    def atan2(y: Double, x: Double): Double
}
