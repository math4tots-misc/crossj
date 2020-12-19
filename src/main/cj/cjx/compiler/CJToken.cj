package cjx.compiler

class CJToken: Repr, Eq {
    # various token types

    # general categories (1-15)
    static val tEof: Int = 1
    static val tDouble: Int = 2
    static val tInt: Int = 3
    static val tId: Int = 4
    static val tChar: Int = 5
    static val tString: Int = 6
    static val tTypeId: Int = 7
    static val tComment: Int = 8

    # multi-character symbols (16-31)
    static val tRshiftu: Int = 16
    static val tEq: Int = 17
    static val tNe: Int = 18
    static val tLe: Int = 19
    static val tGe: Int = 20
    static val tLshift: Int = 21
    static val tRshift: Int = 22
    static val tTruncdiv: Int = 23
    static val tPower: Int = 24
    static val tRightArrow: Int = 25
    static val tPlusEq: Int = 26
    static val tMinusEq: Int = 27
    static val tStarEq: Int = 28
    static val tDivEq: Int = 29
    static val tTruncdivEq: Int = 30
    static val tRemEq: Int = 31

    # token types in the range 32-127 are reserved for ASCII single character
    # token types.

    # keywords (201+)
    static val kwDef: Int = 201
    static val kwClass: Int = 202
    static val kwTrue: Int = 203
    static val kwFalse: Int = 204
    static val kwNull: Int = 205
    static val kwIf: Int = 206
    static val kwElse: Int = 207
    static val kwImport: Int = 208
    static val kwWhile: Int = 209
    static val kwBreak: Int = 210
    static val kwContinue: Int = 211
    static val kwVar: Int = 212
    static val kwVal: Int = 213
    static val kwNew: Int = 214
    static val kwTrait: Int = 215
    static val kwNative: Int = 216
    static val kwStatic: Int = 217
    static val kwPrivate: Int = 218
    static val kwPublic: Int = 219
    static val kwPackage: Int = 220
    static val kwReturn: Int = 221
    static val kwAnd: Int = 222
    static val kwOr: Int = 223
    static val kwIs: Int = 224
    static val kwNot: Int = 225
    static val kwIn: Int = 226
    static val kwThen: Int = 227
    static val kwSwitch: Int = 228
    static val kwCase: Int = 229
    static val kwUnion: Int = 230
    static val kwEnum: Int = 231
    static val kwDefault: Int = 232
    static val kwAs: Int = 233
    static val kwFor: Int = 234
    static val kwAsync: Int = 235
    static val kwAwait: Int = 236

    static val keywordTypes: List[Int] = [
        Self.kwDef,
        Self.kwClass,
        Self.kwTrue,
        Self.kwFalse,
        Self.kwNull,
        Self.kwIf,
        Self.kwElse,
        Self.kwImport,
        Self.kwWhile,
        Self.kwBreak,
        Self.kwContinue,
        Self.kwVar,
        Self.kwVal,
        Self.kwNew,
        Self.kwTrait,
        Self.kwNative,
        Self.kwStatic,
        Self.kwPrivate,
        Self.kwPublic,
        Self.kwPackage,
        Self.kwReturn,
        Self.kwAnd,
        Self.kwOr,
        Self.kwIs,
        Self.kwNot,
        Self.kwIn,
        Self.kwThen,
        Self.kwSwitch,
        Self.kwCase,
        Self.kwUnion,
        Self.kwEnum,
        Self.kwDefault,
        Self.kwAs,
        Self.kwFor,
        Self.kwAsync,
        Self.kwAwait,
    ]

    val type: Int
    val text: String
    val line: Int
    val column: Int

    def of(type: Int, text: String, line: Int, column: Int): Self {
        return Self.new(type, text, line, column)
    }

    def ofInt(text: String, line: Int, column: Int): Self {
        return Self.new(Self.tInt, text, line, column)
    }

    def ofDouble(text: String, line: Int, column: Int): Self {
        return Self.new(Self.tDouble, text, line, column)
    }

    def ofId(text: String, line: Int, column: Int): Self {
        return Self.new(Self.tId, text, line, column)
    }

    def repr(self: Self): String {
        return (
            "CJToken.of(" +
            Self.typeToString(self.type) +
            ", " +
            self.text.repr() +
            ", " +
            self.line +
            ", " +
            self.column +
            ")")
    }

    def __eq(self: Self, other: Self): Bool {
        return (
            self.type == other.type and
            self.text == other.text and
            self.line == other.line and
            self.column == other.column)
    }

    def typeToString(type: Int): String {
        return switch type {
            case Self.tEof = "CJToken.tEof"
            case Self.tDouble = "CJToken.tDouble"
            case Self.tInt = "CJToken.tInt"
            case Self.tId = "CJToken.tId"
            case Self.tChar = "CJToken.tChar"
            case Self.tString = "CJToken.tString"
            case Self.tTypeId = "CJToken.tTypeId"
            case Self.tComment = "CJToken.tComment"
            case Self.tRshiftu = "CJToken.tRshiftu"
            case Self.tEq = "CJToken.tEq"
            case Self.tNe = "CJToken.tNe"
            case Self.tLe = "CJToken.tLe"
            case Self.tGe = "CJToken.tGe"
            case Self.tLshift = "CJToken.tLshift"
            case Self.tRshift = "CJToken.tRshift"
            case Self.tTruncdiv = "CJToken.tTruncdiv"
            case Self.tPower = "CJToken.tPower"
            case Self.tRightArrow = "CJToken.tRightArrow"
            case Self.tPlusEq = "CJToken.tPlusEq"
            case Self.tMinusEq = "CJToken.tMinusEq"
            case Self.tStarEq = "CJToken.tStarEq"
            case Self.tDivEq = "CJToken.tDivEq"
            case Self.tTruncdivEq = "CJToken.tTruncdivEq"
            case Self.tRemEq = "CJToken.tRemEq"
            case Self.kwDef = "CJToken.kwDef"
            case Self.kwClass = "CJToken.kwClass"
            case Self.kwTrue = "CJToken.kwTrue"
            case Self.kwFalse = "CJToken.kwFalse"
            case Self.kwNull = "CJToken.kwNull"
            case Self.kwIf = "CJToken.kwIf"
            case Self.kwElse = "CJToken.kwElse"
            case Self.kwImport = "CJToken.kwImport"
            case Self.kwWhile = "CJToken.kwWhile"
            case Self.kwBreak = "CJToken.kwBreak"
            case Self.kwContinue = "CJToken.kwContinue"
            case Self.kwVar = "CJToken.kwVar"
            case Self.kwVal = "CJToken.kwVal"
            case Self.kwNew = "CJToken.kwNew"
            case Self.kwTrait = "CJToken.kwTrait"
            case Self.kwNative = "CJToken.kwNative"
            case Self.kwStatic = "CJToken.kwStatic"
            case Self.kwPrivate = "CJToken.kwPrivate"
            case Self.kwPublic = "CJToken.kwPublic"
            case Self.kwPackage = "CJToken.kwPackage"
            case Self.kwReturn = "CJToken.kwReturn"
            case Self.kwAnd = "CJToken.kwAnd"
            case Self.kwOr = "CJToken.kwOr"
            case Self.kwIs = "CJToken.kwIs"
            case Self.kwNot = "CJToken.kwNot"
            case Self.kwIn = "CJToken.kwIn"
            case Self.kwThen = "CJToken.kwThen"
            case Self.kwSwitch = "CJToken.kwSwitch"
            case Self.kwCase = "CJToken.kwCase"
            case Self.kwUnion = "CJToken.kwUnion"
            case Self.kwEnum = "CJToken.kwEnum"
            case Self.kwDefault = "CJToken.kwDefault"
            case Self.kwAs = "CJToken.kwAs"
            case Self.kwFor = "CJToken.kwFor"
            case Self.kwAsync = "CJToken.kwAsync"
            case Self.kwAwait = "CJToken.kwAwait"
            case '\n'.toInt() = "'\\n'"
            default = (
                if (type >= 32 or type <= 127)
                    "'" + type.toChar() + "'" else
                    "Unknown(" + type + ")"
            )
        }
    }

    def keywordTypeToString(type: Int): String {
        val name = Self.typeToString(type)
        Assert.withMessage(name.startsWith("CJToken.kw"), name)
        return name.cutFrom("CJToken.kw".size()).lower()
    }
}
