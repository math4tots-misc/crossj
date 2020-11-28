package cj


native class Tuple4[A0, A1, A2, A3] :
        Repr if A0 : Repr and A1 : Repr and A2 : Repr and A3 : Repr,
        Eq if A0 : Eq and A1 : Eq and A2 : Eq and A3 : Eq,
        Ord if A0 : Ord and A1 : Ord and A2 : Ord and A3 : Ord,
        Hash if A0 : Hash and A1 : Hash and A2 : Hash and A3 : Hash {
    def get0(self: Self) : A0
    def get1(self: Self) : A1
    def get2(self: Self) : A2
    def get3(self: Self) : A3

    if A0 : Repr and A1 : Repr and A2 : Repr and A3 : Repr {
        def repr(self: Self) : String
    }

    if A0 : Eq and A1 : Eq and A2 : Eq and A3 : Eq {
        def __eq(self: Self, other: Self) : Bool
    }

    if A0 : Ord and A1 : Ord and A2 : Ord and A3 : Ord {
        def __lt(self: Self, other: Self) : Bool
    }

    if A0 : Hash and A1: Hash and A2 : Hash and A3 : Hash {
        def hash(self: Self) : Int
    }
}
