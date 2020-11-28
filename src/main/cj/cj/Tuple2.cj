package cj


native class Tuple2[A0, A1] :
        Repr if A0 : Repr and A1 : Repr,
        Eq if A0 : Eq and A1 : Eq,
        Ord if A0 : Ord and A1 : Ord,
        Hash if A0 : Hash and A1 : Hash {
    def get0(self: Self) : A0
    def get1(self: Self) : A1

    if A0 : Repr and A1 : Repr {
        def repr(self: Self) : String
    }

    if A0 : Eq and A1 : Eq {
        def __eq(self: Self, other: Self) : Bool
    }

    if A0 : Ord and A1 : Ord {
        def __lt(self: Self, other: Self) : Bool
    }

    if A0 : Hash and A1: Hash {
        def hash(self: Self) : Int
    }
}
