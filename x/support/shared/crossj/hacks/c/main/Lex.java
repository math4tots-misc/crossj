package crossj.hacks.c.main;

import crossj.IO;
import crossj.List;
import crossj.Str;
import crossj.hacks.c.CLexer;
import crossj.hacks.c.Source;

public final class Lex {

    private static final int COL1 = 10;
    private static final int COL2 = 12;
    private static final int COL3 = 8;

    public static void main(String[] args) {
        var argsList = List.fromJavaArray(args);
        var source = argsList.size() == 0 ? Source.of("<stdin>", IO.readStdin())
                : Source.of(argsList.get(0), IO.readFile(argsList.get(0)));
        var lexer = CLexer.getDefault();
        var tokens = lexer.lexAll(source);

        IO.println(
                Str.rpad("type", COL1, " ") + " " + Str.rpad("data", COL2, " ") + " " + Str.rpad("lineno", COL3, " "));

        for (var token : tokens) {
            var type = Str.rpad(token.getType(), COL1, " ");
            var dataobj = token.getData();
            var data = Str.upto(Str.rpad(dataobj == null ? "_" : "" + dataobj, COL2, " "), COL2);
            var lineno = Str.lpad("" + token.getMark().getLineNumber(), COL3, " ");
            IO.println(type + " " + data + " " + lineno);
        }
    }
}
