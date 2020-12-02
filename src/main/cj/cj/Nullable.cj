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
union Nullable[T] {
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
}
