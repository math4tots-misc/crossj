package cj

import cj.IO

union Option[T] {
    case Some(T)
    case None

    def of(t: T) : Self {
        return Self.Some(t)
    }

    def get(self: Self) : T {
        switch union self {
            case Some(t) {
                return t
            }
            case None {
                IO.panic("Get from Option.None")
            }
        }
    }
}
