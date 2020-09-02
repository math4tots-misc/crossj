package other;
import crossj.*;

public final class Main {
    public static void main(String[] args) {
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
    }
}
