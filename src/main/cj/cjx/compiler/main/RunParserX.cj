package cjx.compiler.main

import cj.Process
import cjx.compiler.CJParserX

class RunParserX {
    def main() {
        val args = Process.argv()
        Self._main(args.get(0)).get()
    }

    def _main(path: String): Try[Unit] {
        val data = FS.read(path)?
        val defn = CJParserX.parseString(path, data)?
        IO.println(defn.fullName())
        defn.comment().ifPresent(comment -> {
            IO.println(comment)
        })
        val staticMembers = defn.members().filter(m -> m.isStatic())
        val instanceMembers = defn.members().filter(m -> not m.isStatic())
        for member in staticMembers {
            member.field().ifPresent(field -> {
                IO.println("  static field " + field.name() + ": " + field.type())
            })
        }
        for member in staticMembers {
            member.method().ifPresent(method -> {
                val paramtypes = "(" + ", ".join(method.parameters().map(p -> p.type())) + ")"
                val rtype = method.returnType().map(t -> ": " + t.repr()).getOrDefault("")
                IO.println("  static method " + method.name() + paramtypes + rtype)
            })
        }
        for member in instanceMembers {
            member.field().ifPresent(field -> {
                IO.println("  field " + field.name() + ": " + field.type())
            })
        }
        for member in instanceMembers {
            member.method().ifPresent(method -> {
                val paramtypes = "(" + ", ".join(method.parameters().map(p -> p.type())) + ")"
                val rtype = method.returnType().map(t -> ": " + t.repr()).getOrDefault("")
                IO.println("  method " + method.name() + paramtypes + rtype)
            })
        }
        return Try.Ok(())
    }
}
