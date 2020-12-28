package cjx.compiler

import cj.Process
import cjx.compiler.CJContext

class CJJSTranslator {
    val _ctx: CJContext
    val _out: StringBuilder

    def init(): Self {
        return Self.initWithContext(CJContext.init())
    }

    def initWithContext(ctx: CJContext): Self {
        return Self.new(ctx, String.builder())
    }

    def ctx(self: Self): CJContext {
        return self._ctx
    }

    def main() {
        Self._main().get()
    }

    def _main(): Try[Unit] {
        val args = Process.argv()
        var out = "out/cj2.js"
        var mode = "normal"
        val sourceRoots = @["src/main/cj", "src/test/cj"]
        var nullableMaincls = Nullable[String].None
        for arg in args {
            switch mode {
                case "normal" {
                    switch arg {
                        case "--out"
                        case "-o" {
                            mode = "--out"
                        }
                        case "--source-root"
                        case "-r" {
                            mode = "--source-root"
                        }
                        default {
                            union nullableMaincls {
                                case None {
                                    nullableMaincls = Nullable.Some(arg)
                                }
                                case Some(mc) {
                                    IO.panic(
                                        "More than one main class specified (" +
                                        mc.repr() +
                                        ", " +
                                        arg.repr() +
                                        ")")
                                }
                            }
                        }
                    }
                }
                case "--out" {
                    out = arg
                }
                case "--source-root" {
                    sourceRoots.add(arg)
                }
                default {
                    IO.panic("Invalid mode: " + mode.repr())
                }
            }
        }
        val maincls = union nullableMaincls {
            case Some(mc) = mc
            case None = IO.panic("main class not specified")
        }
        val ctx = CJContext.init()
        for sourceRoot in sourceRoots {
            ctx.addSourceRoot(sourceRoot)
        }
        ctx.loadAutoImportedItems()?
        val items = ctx.loadItemDeps(maincls)?

        for item in items {
            IO.println(item.fullName())
        }

        val translator = Self.initWithContext(ctx)

        return Try.Ok(())
    }
}
