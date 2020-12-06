package hacks.misc


class FSDemo {
    def main() {
        Self._main().get()
    }

    def _main(): Try[Unit] {
        IO.println(FS.cwd())
        IO.println(FS.list(".")?)
        return Try.Ok(())
    }
}
