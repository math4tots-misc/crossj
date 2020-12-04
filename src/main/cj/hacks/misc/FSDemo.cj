package cj.hacks.misc


class FSDemo {
    def main() {
        Self._main().get()
    }

    def _main(): Try[Unit] {
        IO.println(FS.list(".")?)
        return Try.Ok(())
    }
}
