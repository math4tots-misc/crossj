package cj


native class Promise[T] {
    def all(promises: List[Promise[T]]): Promise[List[T]]
    def all2[B](promises: Tuple[Promise[T], Promise[B]]): Promise[Tuple[T, B]]
    def all3[B, C](promises: Tuple[Promise[T], Promise[B], Promise[C]]): Promise[Tuple[T, B, C]]
    def all4[B, C, D](promises: Tuple[
        Promise[T], Promise[B], Promise[C], Promise[D],
    ]): Promise[Tuple[T, B, C, D]]

    def resolve(t: T): Self
    def reject(error: Error): Self
    def err(message: String): Self

    def map[R](self: Self, f: Fn[R, T]): Promise[R]
    def flatMap[R](self: Self, f: Fn[Promise[R], T]): Promise[R]
    def recover(self: Self, f: Fn[T, Error]): Self
    def finally(self: Self, f: Fn[Unit]): Promise[Unit]

    def onResolve(self: Self, f: Fn[Unit, T]): Promise[Unit]
    def onFail(self: Self, f: Fn[Unit, Error]): Promise[Unit]
}
