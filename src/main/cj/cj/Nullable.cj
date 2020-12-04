package cj


# Nullable type
#
# This type is treated a bit specially and has some extra restrictions.
#
# In particular `Nullable` types may not be used in a type argument.
#
# It is ok for the backends to treat Nullable like a normal union, but
# the extra restriction allows it to take advantage of any 'null' types
# the underlying target may have.
#
union Nullable[T] :
        Eq if T : Eq,
        Repr if T : Repr,
        ToBool {
    case Some(T)
    case None

    # DEPRECATED: Use Nullable.Some instead
    def of(self: Self, t: T): Nullable[T] {
        return Self.Some(t)
    }

    def map[R](self: Self, f: Fn[R, T]): Nullable[R] {
        union self {
            case Some(t) {
                return Nullable.Some(f.call(t))
            }
            case None {
                return Nullable.None
            }
        }
    }

    def get(self: Self) : T {
        union self {
            case Some(t) {
                return t
            }
            case None {
                IO.panic("Get from None")
            }
        }
    }

    def toBool(self: Self) : Bool {
        return union self {
            case Some(t) = true
            case None = false
        }
    }

    def isPresent(self: Self) : Bool {
        return self.toBool()
    }

    def isEmpty(self: Self) : Bool {
        return not self.isPresent()
    }

    if T: Eq {
        def __eq(self: Self, other: Self): Bool {
            return union self {
                case Some(t1) = union other {
                    case Some(t2) = t1 == t2
                    case None = false
                }
                case None = other.isEmpty()
            }
        }
    }

    if T : Repr {
        def repr(self: Self): String {
            return union self {
                case Some(t) = "Some(" + t.repr() + ")"
                case None = "None"
            }
        }
    }
}
