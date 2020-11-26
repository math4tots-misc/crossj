package cj


trait ToList[T] {
    def toList(x: Self) : List[T]
}
