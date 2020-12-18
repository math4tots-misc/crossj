package cjx.hacks.misc

import cj.IO
import cj.Time

#
# Dead simple performance test
#
class BigLoop {

    def main() {
        var start = Time.now()
        var i = 0
        var total = 0
        var limit = (10.0 ** 8.0).toInt()
        IO.println("limit = " + limit.toString())
        while i < limit {
            total = total + i
            i = i + 1
        }
        IO.println("total = " + total.toString())
        var end = Time.now()
        IO.println("running time = " + (end - start).toString() + "s")
    }
}
