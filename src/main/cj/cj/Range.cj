package cj

## Represents a range of numbers
native class Range: Iterable[Int] {
    def upto(end: Int): Self
    def of(start: Int, end: Int): Self
    def withStep(start: Int, end: Int, step: Int): Self
    def iter(self: Self): Iterator[Int]
}
