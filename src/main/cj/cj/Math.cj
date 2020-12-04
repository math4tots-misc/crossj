package cj


import cj.Ring
import cj.MathImpl


## Generally what you would expect in a programming language's builtin
## Math lib
class Math {
    static val e: Double = MathImpl.e()
    static val pi: Double = MathImpl.pi()

    def min[T: Ord](a: T, b: T): T {
        return if (a < b) a else b
    }
    def max[T: Ord](a: T, b: T): T {
        return if (a < b) b else a
    }
    def minOf[T: Ord, C: Iterable[T]](ts: C): T {
        var best = Nullable[T].None
        for t in ts {
            best = union best {
                case Some(b) = if (t < b) Nullable.Some(t) else best
                case None = Nullable.Some(t)
            }
        }
        union best {
            case Some(t) {
                return t
            }
            case None {
                IO.panic("minOf empty iterable")
            }
        }
    }
    def maxOf[T: Ord, C: Iterable[T]](ts: C): T {
        var best = Nullable[T].None
        for t in ts {
            best = union best {
                case Some(b) = if (b < t) Nullable.Some(t) else best
                case None = Nullable.Some(t)
            }
        }
        union best {
            case Some(t) {
                return t
            }
            case None {
                IO.panic("maxOf empty iterable")
            }
        }
    }
    def abs[T: Ring & Ord](t: T): T {
        return Self.max(T.zero(), t)
    }

    ## Returns a pseudorandom number
    ## greater than or equal to 0.0 and less than 1.0
    def random(): Double {
        return MathImpl.random()
    }

    def ceil(x: Double): Int {
        return MathImpl.ceil(x)
    }
    def floor(x: Double): Int {
        return MathImpl.floor(x)
    }

    ## Computes the sine of an angle given in randians
    def sin(angle: Double): Double {
        return MathImpl.sin(angle)
    }
    ## Computes the cosine of an angle given in randians
    def cos(angle: Double): Double {
        return MathImpl.cos(angle)
    }
    ## Computes the tangent of an angle given in randians
    def tan(angle: Double): Double {
        return MathImpl.tan(angle)
    }
    ## Computes the argsine of an angle given in randians
    def asin(angle: Double): Double {
        return MathImpl.asin(angle)
    }
    ## Computes the argcosine of an angle given in randians
    def acos(angle: Double): Double {
        return MathImpl.acos(angle)
    }
    ## Computes the argtangent of an angle given in randians
    def atan(angle: Double): Double {
        return MathImpl.atan(angle)
    }
    ## Returns the angle theta from the conversion of rectangular
    ## coordinates (x, y) to polar coordinates (r, theta)
    def atan2(y: Double, x: Double): Double {
        return MathImpl.atan2(y, x)
    }
    ## Computes the square root of a number
    def sqrt(x: Double): Double {
        return x ** 0.5
    }
}
