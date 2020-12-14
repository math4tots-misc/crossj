package cjx.html

import cjx.JSObject
import cjx.JSWrapper

trait Event: JSWrapper {
    def target(self: Self): JSObject {
        return self.obj().field("target")
    }
}
