package other;
import crossj.*;

public final class Main {
    public static void main(String[] args) {
        {
            IO.println("Hello world!");
            IO.println(123 + 24);
            IO.println(24 / 7);
            IO.println(24 / 7.0);
            IO.println("value = " + -24 / 7.0);
            double x = 5;
            IO.println("x = " + x);
            IO.println("x / 2 = " + x / 2);
            IO.println("list = " + List.of(1, 2, 3));
            IO.println("strlist = " + List.of("hello", "world"));
            IO.println("strlist = " + List.of("hello\n", "\tworld"));
            IO.println("\thello\n");
            IO.println("list1.equals(list2) = " + List.of("hello").equals(List.of("hi")));
            IO.println("list1.equals(list2) = " + List.of("hello").equals(List.of("hello")));
            Foo foo1 = new Foo();
            IO.println("foo1.hashCode() = " + foo1.hashCode());
            Foo foo2 = new Foo();
            IO.println("foo2.hashCode() = " + foo2.hashCode());
            IO.println("repr(s) = " + Repr.of("hello"));
        }
        {
            Pair<String, String> pair1 = Pair.of("abc", "xyz");
            Pair<String, String> pair2 = Pair.of("ggg", "hhh");
            IO.println("pair1 == pair1 = " + pair1.equals(pair1));
            IO.println("pair1 == pair2 = " + pair1.equals(pair2));
            IO.println("pair1 == <string> = " + pair1.equals("abcxyz"));
        }
        {
            IO.println("list.map = " + List.of(1, 2).map(x -> x + 1));
            Func1<String, String> f = Func1.of(x -> x + "hi");
            IO.println("f.apply = " + f.apply("asdf"));
        }
        {
            Func1<Integer, Integer> f = Func1.of(x -> x + 2);
            IO.println("f.apply(Integer) = " + f.apply(24));
        }
    }
}
