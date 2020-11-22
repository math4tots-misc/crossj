package cj

class MutableList[T] :
        Ord if T : Ord,
        Eq if T : Eq {

    def get(i: Int) : T

    def set(i: Int, t: T) : Unit
}
