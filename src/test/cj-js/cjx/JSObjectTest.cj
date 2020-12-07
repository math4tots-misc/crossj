package cjx

import cjx.JSObject

class JSObjectTest {
    def testMisc() {
        {
            val list = JSObject.from([1, 2, 3])
            {
                val len = list.field("length").asInt()
                Assert.equal(len, 3)
            }

            list.method("push", [JSObject.from("Hello")])
            {
                val len = list.field("length").asInt()
                Assert.equal(len, 4)
            }

            {
                val x = list.get(0).asInt()
                Assert.equal(x, 1)
            }
            {
                val x = list.get(3).asString()
                Assert.equal(x, "Hello")
            }
            {
                Assert.equal(list.typeOf(), "object")
                Assert.equal(list.get(0).typeOf(), "number")
                Assert.equal(list.get(1).typeOf(), "number")
                Assert.equal(list.get(2).typeOf(), "number")
                Assert.equal(list.get(3).typeOf(), "string")
            }
        }
    }

    def testCreate() {
        {
            val null_ = JSObject.null_()
            val obj1 = JSObject.create(null_)
            val obj2 = JSObject.create(null_)
            Assert.that(obj1 != obj2)

            Assert.equal(obj1.toString(), "{}")
            obj1.setField("foo", "hello")
            Assert.equal(obj1.toString(), "{\"foo\":\"hello\"}")
        }
    }
}
