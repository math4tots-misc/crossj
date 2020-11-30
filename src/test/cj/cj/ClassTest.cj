package cj

import cj.Assert
import cj.dat.SampleClass


class ClassTest {

    def testFieldAccess() {
        {
            var obj = SampleClass.new(24)
            Assert.equal(obj.sampleField, 24)
        }
    }
}
