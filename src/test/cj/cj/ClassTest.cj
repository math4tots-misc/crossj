package cj

import cj.dat.SampleClass
import cj.dat.SampleClass2


class ClassTest {
    def testFieldAccess() {
        {
            # read-only
            var obj = SampleClass.new(24)
            Assert.equal(obj.sampleField, 24)
        }
        {
            # read/write
            var obj = SampleClass2.new("hi")
            Assert.equal(obj.someField, "hi")
            obj.someField = "new field value"
            Assert.equal(obj.someField, "new field value")
        }
        {
            # read-only static field
            Assert.equal(SampleClass.sampleStaticField, "some static field")
        }
        {
            # mutable static fields
            Assert.equal(SampleClass.sampleMutableStaticField, "mutable static field")
            SampleClass.sampleMutableStaticField = "asdfasdf"
            Assert.equal(SampleClass.sampleMutableStaticField, "asdfasdf")
        }
    }
}
