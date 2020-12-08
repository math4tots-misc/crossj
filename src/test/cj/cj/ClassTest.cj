package cj

import cj.dat.SampleClass
import cj.dat.SampleClass2
import cj.dat.SampleWrapperClass


class ClassTest {
    def testFieldAccess() {
        {
            # read-only
            var obj = SampleClass.new(24, "qwerty")
            Assert.equal(obj.sampleField, 24)
            Assert.equal(obj.anotherSampleField, "qwerty")
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

    def testWrapperClass() {
        {
            # read-only
            var obj = SampleWrapperClass.new(24)
            Assert.equal(obj.sampleField, 24)
        }
        {
            # read-only static field
            Assert.equal(SampleWrapperClass.sampleStaticField, "some static field")
        }
        {
            # mutable static fields
            Assert.equal(SampleWrapperClass.sampleMutableStaticField, "mutable static field")
            SampleWrapperClass.sampleMutableStaticField = "asdfasdf"
            Assert.equal(SampleWrapperClass.sampleMutableStaticField, "asdfasdf")
        }
    }
}
