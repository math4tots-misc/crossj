
class MC$cj$Int {
    M$__eq(a, b) {
        return a === b;
    }
    M$__lt(a, b) {
        return a < b;
    }
    M$__hash(x) {
        return x|0;
    }
    M$__add(a, b) {
        return (a + b)|0;
    }
    M$__sub(a, b) {
        return (a - b)|0;
    }
    M$__mul(a, b) {
        return (a * b)|0;
    }
    M$__div(a, b) {
        return (a / b)|0;
    }
    M$__mod(a, b) {
        return (a % b)|0;
    }
}
const MO$cj$Int = new MC$cj$Int();

class MC$cj$IO {
    M$println(string) {
        console.log(string);
    }
}
const MO$cj$IO = new MC$cj$IO();
