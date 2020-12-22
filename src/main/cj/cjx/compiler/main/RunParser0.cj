package cjx.compiler.main

import cjx.compiler.CJIRItemInfo
import cjx.compiler.CJParser0
import cj.Process

class RunParser0 {
    def main() {
        val args = Process.argv()
        val rec = Self.rec(args.get(0))
        Self._main(args.get(0)).get()
    }

    def _main(start: String): Try[Unit] {
        val infos = Self.list(start)?
        for info in infos {
            val tps = info.typeParameters()
            val tp = if (tps.isEmpty()) "" else "[" + ", ".join(tps.map(t -> t.name())) + "]"
            IO.println(info.path() + " -> " + info.fullName() + tp)
            for imp in info.imports() {
                IO.println("  import " + imp)
            }
        }
        return Try.Ok(())
    }

    def list(start: String): Try[List[CJIRItemInfo]] {
        val b = List[CJIRItemInfo].builder()
        for path in Self.rec(start)? {
            if path.endsWith(".cj") {
                val data = FS.read(path)?
                val info = CJParser0.parseString(path, data)?
                b.add(info)
            }
        }
        return Try.Ok(b.build())
    }

    def rec(path: String): Try[List[String]] {
        val out = List[String].builder()
        val stack = @[path]
        while stack.toBool() {
            val p = stack.pop()
            val children = FS.list(p)?.sorted().map(base -> FS.join([p, base]))
            out.addAll(children.filter(c -> FS.isfile(c)))
            stack.addAll(children.filter(c -> FS.isdir(c)).reversed())
        }
        return Try.Ok(out.build())
    }
}
