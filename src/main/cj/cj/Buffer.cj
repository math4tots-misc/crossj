package cj


native class Buffer : Repr, Eq, Clone {
    def withCapacity(capacity: Int) : Self
    def withSize(size: Int) : Self
    def ofU8s(u8s: List[Int]) : Self
    def ofI8s(i8s: List[Int]) : Self
    def ofI32LEs(i32s: List[Int]) : Self
    def ofI32BEs(i32s: List[Int]) : Self
    def fromASCII(text: String) : Self

    def repr(self: Self) : String
    def __eq(self: Self, other: Self) : Bool
    def size(self: Self) : Int

    def useLittleEndian(self: Self, flag: Bool)
    def useBigEndian(self: Self, flag: Bool)
    def usingLittleEndian(self: Self) : Bool
    def usingBigEndian(self: Self) : Bool

    def getI8(self: Self, i: Int) : Int
    def setI8(self: Self, i: Int, data: Int)
    def addI8(self: Self, data: Int)
    def getU8(self: Self, i: Int) : Int
    def setU8(self: Self, i: Int, data: Int)
    def addU8(self: Self, data: Int)
    def getI16(self: Self, i: Int) : Int
    def setI16(self: Self, i: Int, data: Int)
    def addI16(self: Self, data: Int)
    def getU16(self: Self, i: Int) : Int
    def setU16(self: Self, i: Int, data: Int)
    def addU16(self: Self, data: Int)
    def getI32(self: Self, i: Int) : Int
    def setI32(self: Self, i: Int, data: Int)
    def addI32(self: Self, data: Int)
    def getF32(self: Self, i: Int) : Double
    def setF32(self: Self, i: Int, data: Double)
    def addF32(self: Self, data: Double)
    def getF64(self: Self, i: Int) : Double
    def setF64(self: Self, i: Int, data: Double)
    def addF64(self: Self, data: Double)
    def getU32AsDouble(self: Self, i: Int) : Double
    def setU32WithDouble(self: Self, i: Int, data: Double)
    def addU32WithDouble(self: Self, data: Double)

    def addASCII(self: Self, string: String)
    def addBuffer(self: Self, other: Buffer)

    def slice(self: Self, start: Int, end: Int) : Buffer

    def asI8s(self: Self) : Iterator[Int]
    def asU8s(self: Self) : Iterator[Int]

    def clone(self: Self) : Self
}
