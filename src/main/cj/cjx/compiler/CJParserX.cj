package cjx.compiler

import cjx.compiler.CJAstCaseDefinition
import cjx.compiler.CJAstTypeExpression
import cjx.compiler.CJAstAssignmentTarget
import cjx.compiler.CJAstStatement
import cjx.compiler.CJAstExpression
import cjx.compiler.CJAstFieldDefinition
import cjx.compiler.CJAstMethodDefinition
import cjx.compiler.CJIRModifier
import cjx.compiler.CJAstItemMemberDefinition
import cjx.compiler.CJAstTypeCondition
import cjx.compiler.CJAstParameter
import cjx.compiler.CJAstTraitExpression
import cjx.compiler.CJAstTypeParameter
import cjx.compiler.CJIRItemKind
import cjx.compiler.CJAstImport
import cjx.compiler.CJLexer
import cjx.compiler.CJAstItemDefinition
import cjx.compiler.CJToken
import cjx.compiler.CJParserMixin

class CJParserX: CJParserMixin {

    private static val logicalNotBindingPower: Int = Self.tokenPrecedence(CJToken.tEq) + 5
    private static val defaultUnaryOpBindingPower: Int = Self.tokenPrecedence('*'.toInt()) + 5

    def parseString(filename: String, data: String): Try[CJAstItemDefinition] {
        val tokens = CJLexer.lex(data).addContext(filename)?
        return Self.init(filename, tokens).parseAll()
    }

    val _path: String
    val _tokens: List[CJToken]
    val _i: Int

    def init(filename: String, tokens: List[CJToken]): Self {
        return Self.new(filename, tokens, 0)
    }

    def path(self: Self): String {
        return self._path
    }

    def tokens(self: Self): List[CJToken] {
        return self._tokens
    }

    def tell(self: Self): Int {
        return self._i
    }

    def seek(self: Self, i: Int) {
        self._i = i
    }

    def skipsComments(self: Self): Bool {
        return false
    }

    private def parseAll(self: Self): Try[CJAstItemDefinition] {
        self.skipDelimiters()
        self.expect(CJToken.kwPackage)?
        val pkg = self.expectPackageName()?
        val imports = List[CJAstImport].builder()
        self.skipDelimiters()
        while self.at(CJToken.kwImport) {
            val mark = self.mark()
            self.next()
            val name = self.expectQualifiedName()?
            imports.add(CJAstImport.new(mark, name))
            self.skipDelimiters()
        }
        val comment = (
            if (self.at(CJToken.tComment))
                Nullable.Some(self.expect(CJToken.tComment).get().text) else
                Nullable.None)
        self.skipDelimiters()
        return self.expectItemDefinition(pkg, imports.build(), comment)
    }

    private def expectItemDefinition(
            self: Self,
            pkg: String,
            imports: List[CJAstImport],
            comment: Nullable[String]): Try[CJAstItemDefinition] {
        val modifiers = self.parseModifiers()
        val kind = (
            if (self.consume(CJToken.kwTrait)) CJIRItemKind.Trait
            else if (self.consume(CJToken.kwUnion)) CJIRItemKind.Union
            else {
                self.expect(CJToken.kwClass)?
                CJIRItemKind.Class
            })
        val mark = self.mark()
        val shortName = self.expectTypeId()?
        val typeParameters = self.expectTypeParameters()?
        val traits = List[Tuple[CJAstTraitExpression, List[CJAstTypeCondition]]].builder()
        self.skipDelimiters()
        if self.consumeChar(':') {
            self.skipDelimiters()
            var repeat = true
            while repeat {
                val trait_ = self.expectTraitExpression()?
                val cond = (
                    if (self.at(CJToken.kwIf)) self.expectTypeConditions()?
                    else [])
                traits.add((trait_, cond))
                self.skipDelimiters()
                repeat = self.consumeChar(',')
                self.skipDelimiters()
            }
        }
        self.skipDelimiters()
        self.expectChar('{')?
        val members = List[CJAstItemMemberDefinition].builder()
        while not self.consumeChar('}') {
            if self.at(CJToken.kwIf) {
                val conditions = self.expectTypeConditions()?
                self.skipDelimiters()
                self.expectChar('{')?
                val memberComment = self.maybeComment()
                self.skipDelimiters()
                while not self.consumeChar('}') {
                    val memberModifiers = self.parseModifiers()
                    val method = self.expectMethodDefinition(
                        conditions, memberComment, memberModifiers)?
                    members.add(CJAstItemMemberDefinition.Method(method))
                    self.skipDelimiters()
                }
            } else {
                self.skipDelimiters()
                val memberComment = self.maybeComment()
                self.skipDelimiters()
                val member = self.expectClassMember(memberComment)?
                members.add(member)
            }
            self.skipDelimiters()
        }

        return Try.Ok(CJAstItemDefinition.new(
            mark,
            pkg,
            imports,
            comment,
            modifiers,
            shortName,
            typeParameters,
            traits.build(),
            members.build(),
        ))
    }

    def expectTypeParameters(self: Self): Try[List[CJAstTypeParameter]] {
        val tparams = List[CJAstTypeParameter].builder()
        if self.consumeChar('[') {
            while not self.consumeChar(']') {
                tparams.add(self.expectTypeParameter()?)
                if not self.consumeChar(',') and not self.atChar(']') {
                    self.expectChar(']')
                }
            }
        }
        return Try.Ok(tparams.build())
    }

    def expectTypeParameter(self: Self): Try[CJAstTypeParameter] {
        val mark = self.mark()
        val name = self.expectTypeId()?
        var bounds = (
            if (self.consumeChar(':')) self.expectTraitExpressionSeq()?
            else [])
        return Try.Ok(CJAstTypeParameter.new(mark, name, bounds))
    }

    def expectTraitExpressionSeq(self: Self): Try[List[CJAstTraitExpression]] {
        val traits = List[CJAstTraitExpression].builder()
        traits.add(self.expectTraitExpression()?)
        while self.consumeChar('&') {
            traits.add(self.expectTraitExpression()?)
        }
        return Try.Ok(traits.build())
    }

    def expectTypeConditions(self: Self): Try[List[CJAstTypeCondition]] {
        self.expect(CJToken.kwIf)?
        val conditions = List[CJAstTypeCondition].builder()
        conditions.add(self.expectTypeCondition()?)
        while self.consume(CJToken.kwAnd) {
            conditions.add(self.expectTypeCondition()?)
        }
        return Try.Ok(conditions.build())
    }

    def expectTypeCondition(self: Self): Try[CJAstTypeCondition] {
        val mark = self.mark()
        val type = self.expectTypeExpression()?
        self.expectChar(':')?
        val traits = self.expectTraitExpressionSeq()?
        return Try.Ok(CJAstTypeCondition.new(mark, type, traits))
    }

    def expectClassMember(self: Self, comment: Nullable[String]): Try[CJAstItemMemberDefinition] {
        val modifiers = self.parseModifiers()
        switch self.peek().map(t -> t.type).getOrDefault(0) {
            case CJToken.kwCase {
                return Try.Ok(CJAstItemMemberDefinition.Case(
                    self.expectCaseDefinition(comment, modifiers)?))
            }
            case CJToken.kwVal
            case CJToken.kwVar {
                return Try.Ok(CJAstItemMemberDefinition.Field(
                    self.expectFieldDefinition(comment, modifiers)?))
            }
            case CJToken.kwDef {
                return Try.Ok(CJAstItemMemberDefinition.Method(
                    self.expectMethodDefinition([], comment, modifiers)?))
            }
            default {
                return self.failExpected("case, field or method definition")
            }
        }
    }

    def expectCaseDefinition(
            self: Self,
            comment: Nullable[String],
            modifiers: List[CJIRModifier]): Try[CJAstCaseDefinition] {
        self.expect(CJToken.kwCase)?
        val mark = self.mark()
        val name = self.expectTypeId()?
        val types = List[CJAstTypeExpression].builder()
        if self.consumeChar('(') {
            while not self.consumeChar(')') {
                types.add(self.expectTypeExpression()?)
                if not self.consumeChar(',') and not self.atChar(')') {
                    self.expectChar(')')?
                }
            }
        }
        return Try.Ok(CJAstCaseDefinition.new(mark, comment, modifiers, name, types.build()))
    }

    def expectFieldDefinition(
            self: Self,
            comment: Nullable[String],
            modifiers: List[CJIRModifier]): Try[CJAstFieldDefinition] {
        val mutable = self.at(CJToken.kwVar)
        if not self.consume(CJToken.kwVar) {
            self.expect(CJToken.kwVal)?
        }
        val mark = self.mark()
        val name = self.expectId()?
        self.expectChar(':')?
        val type = self.expectTypeExpression()?
        val expression = (
            if (self.consumeChar('=')) Nullable.Some(self.expectExpression()?)
            else Nullable.None)
        if expression.isPresent() != (CJIRModifier.Static in modifiers) {
            return self.fail("A field should have an initializer iff it is static")
        }
        return Try.Ok(CJAstFieldDefinition.new(
            mark, comment, modifiers, mutable, name, type, expression))
    }

    def expectMethodDefinition(
            self: Self,
            conditions: List[CJAstTypeCondition],
            comment: Nullable[String],
            modifiers: List[CJIRModifier]): Try[CJAstMethodDefinition] {
        self.expect(CJToken.kwDef)?
        val mark = self.mark()
        val name = self.expectId()?
        val typeParameters = self.expectTypeParameters()?
        val parameters = self.expectParameters()?
        val returnType = (
            if (self.consumeChar(':')) Nullable.Some(self.expectTypeExpression()?)
            else Nullable.None)

        val body: Nullable[CJAstStatement] = (
            if (self.atChar('{')) Nullable.Some(self.expectBlockStatement()?)
            else Nullable.None)
        return Try.Ok(CJAstMethodDefinition.new(
            mark,
            conditions,
            comment,
            modifiers,
            name,
            typeParameters,
            parameters,
            returnType,
            body,
        ))
    }

    def expectParameters(self: Self): Try[List[CJAstParameter]] {
        self.expectChar('(')?
        val list = List[CJAstParameter].builder()
        while not self.consumeChar(')') {
            list.add(self.expectParameter()?)
            if not self.consumeChar(',') and not self.atChar(')') {
                self.expectChar(')')?
            }
        }
        return Try.Ok(list.build())
    }

    def expectParameter(self: Self): Try[CJAstParameter] {
        val mutable = self.consume(CJToken.kwVar)
        val mark = self.mark()
        val name = self.expectId()?
        self.expectChar(':')?
        val type = self.expectTypeExpression()?
        return Try.Ok(CJAstParameter.new(mark, mutable, name, type))
    }

    def expectAssignmentTarget(self: Self): Try[CJAstAssignmentTarget] {
        val mark = self.mark()
        switch self.peek().map(t -> t.type).getOrDefault(0) {
            case CJToken.tId {
                val name = self.expectId()?
                return Try.Ok(CJAstAssignmentTarget.Name(mark, name))
            }
            case '('.toInt() {
                self.next()
                val subtargets = List[CJAstAssignmentTarget].builder()
                while not self.consumeChar(')') {
                    subtargets.add(self.expectAssignmentTarget()?)
                    if not self.consumeChar(',') and not self.atChar(')') {
                        self.expectChar(')')?
                    }
                }
                return Try.Ok(CJAstAssignmentTarget.Tuple(mark, subtargets.build()))
            }
            default {
                return self.failExpectedWithMark("assignment target", mark)
            }
        }
    }

    def expressionToAssignmentTarget(self: Self, expr: CJAstExpression): Try[CJAstAssignmentTarget] {
        return union expr {
            case Name(mark, name) = Try.Ok(CJAstAssignmentTarget.Name(mark, name))
            case Tuple(mark, exprs) = Try.Ok(CJAstAssignmentTarget.Tuple(
                mark, Try.list(exprs.iter().map(e -> self.expressionToAssignmentTarget(e)))?))
            case Field(mark, owner, name) = Try.Ok(CJAstAssignmentTarget.Field(
                mark, owner, name))
            case StaticField(mark, owner, name) = Try.Ok(CJAstAssignmentTarget.StaticField(
                mark, owner, name))
            default = self.failExpectedWithMark("extended assignment target", expr.mark())
        }
    }

    def expectStatement(self: Self): Try[CJAstStatement] {
        val mark = self.mark()
        switch self.peek().map(t -> t.type).getOrDefault(0) {
            case '{'.toInt() {
                return self.expectBlockStatement()
            }
            case CJToken.kwIf {
                return self.expectIfStatement()
            }
            case CJToken.kwWhile {
                self.next()
                val cond = self.expectExpression()?
                val body = self.expectBlockStatement()?
                return Try.Ok(CJAstStatement.While(mark, cond, body))
            }
            case CJToken.kwFor {
                self.next()
                if self.atChar(';') or self.at(CJToken.tId) and self.atOffset('='.toInt(), 1) {
                    # classic for loop
                    var name = Nullable[String].None
                    var startexpr = Nullable[CJAstExpression].None
                    if self.at(CJToken.tId) {
                        name = Nullable.Some(self.expectId()?)
                        self.expectChar('=')?
                        startexpr = Nullable.Some(self.expectExpression()?)
                    }
                    self.expectChar(';')?
                    val condexpr = (
                        if (self.atChar(';')) CJAstExpression.Bool(mark, "true")
                        else self.expectExpression()?)
                    self.expectChar(';')?
                    val incr = (
                        if (self.atChar('{')) Nullable[CJAstStatement].None
                        else {
                            val stmt = self.expectStatement()?
                            union stmt {
                                case Expression(imark, expr) {}
                                case Assignment(imark, target, expr) {}
                                case AugmentedAssignment(imark, target, kind, expr) {}
                                default {
                                    return self.failWithMark(
                                        "Only assignment, augmented assignment or " +
                                        "expression statements are allowed here",
                                        mark)
                                }
                            }
                            Nullable.Some(stmt)
                        })
                    val body = self.expectBlockStatement()?
                    val stmts = List[CJAstStatement].builder()
                    name.ifPresent(n -> {
                        stmts.add(CJAstStatement.VariableDeclaration(
                            mark,
                            true,
                            CJAstAssignmentTarget.Name(mark, n),
                            Nullable[CJAstTypeExpression].None,
                            startexpr.get()))
                    })
                    {
                        val whileBody = List[CJAstStatement].builder()
                        union body {
                            case Block(bmark, bstmts) {
                                whileBody.addAll(bstmts)
                            }
                        }
                        incr.ifPresent(inc -> {
                            whileBody.add(inc)
                        })
                        stmts.add(CJAstStatement.While(mark, condexpr, whileBody.build()))
                    }
                    return Try.Ok(CJAstStatement.Block(mark, stmts.build()))
                } else {
                    val target = self.expectAssignmentTarget()?
                    self.expect(CJToken.kwIn)?
                    val container = self.expectExpression()?
                    val body = self.expectBlockStatement()?
                    return Try.Ok(CJAstStatement.For(mark, target, container, body))
                }
            }
            case CJToken.kwReturn {
                self.next()
                val expr = self.expectExpression()?
                self.expectDelimiter()?
                return Try.Ok(CJAstStatement.Return(mark, expr))
            }
            case CJToken.kwVal
            case CJToken.kwVar {
                val mutable = self.next().get().type == CJToken.kwVar
                val target = self.expectAssignmentTarget()?
                val type = (
                    if (self.consumeChar(':')) Nullable.Some(self.expectTypeExpression()?)
                    else Nullable.None)
                self.expectChar('=')?
                val valexpr = self.expectExpression()?
                return Try.Ok(CJAstStatement.VariableDeclaration(
                    mark, mutable, target, type, valexpr))
            }
            case CJToken.kwUnion {
                self.next()
                val target = self.expectExpression()?
                self.skipDelimiters()
                self.expectChar('{')?
                self.skipDelimiters()
                val cases = List[Tuple[String, List[String], CJAstStatement]].builder()
                while self.consume(CJToken.kwCase) {
                    val name = self.expectTypeId()?
                    val valueNames = List[String].builder()
                    if self.consumeChar('(') {
                        while not self.consumeChar(')') {
                            valueNames.add(self.expectId()?)
                            if not self.consumeChar(',') and not self.atChar(')') {
                                self.expectChar(')')?
                            }
                        }
                    }
                    val body = self.expectBlockStatement()?
                    self.skipDelimiters()
                    cases.add((name, valueNames.build(), body))
                }
                val alt = (
                    if (self.consume(CJToken.kwDefault))
                        Nullable.Some(self.expectBlockStatement()?)
                    else Nullable.None)
                self.skipDelimiters()
                self.expectChar('}')?
                return Try.Ok(CJAstStatement.Union(mark, target, cases.build(), alt))
            }
            case CJToken.kwSwitch {
                self.next()
                val target = self.expectExpression()?
                self.skipDelimiters()
                self.expectChar('{')?
                self.skipDelimiters()
                val cases = List[Tuple[List[CJAstExpression], CJAstStatement]].builder()
                while self.at(CJToken.kwCase) {
                    val valexprs = List[CJAstExpression].builder()
                    while self.consume(CJToken.kwCase) {
                        valexprs.add(self.expectExpression()?)
                        self.skipDelimiters()
                    }
                    val body = self.expectBlockStatement()?
                    self.skipDelimiters()
                    cases.add((valexprs.build(), body))
                }
                val alt = (
                    if (self.consume(CJToken.kwDefault))
                        Nullable.Some(self.expectBlockStatement()?)
                    else Nullable.None)
                self.skipDelimiters()
                self.expectChar('}')?
                return Try.Ok(CJAstStatement.Switch(mark, target, cases.build(), alt))
            }
            default {
                val expr = self.expectExpression()?
                val imark = self.mark()
                switch self.peek().map(t -> t.type).getOrDefault(0) {
                    case '='.toInt() {
                        self.next()
                        val target = self.expressionToAssignmentTarget(expr)?
                        val valexpr = self.expectExpression()?
                        return Try.Ok(CJAstStatement.Assignment(imark, target, valexpr))
                    }
                    case CJToken.tPlusEq
                    case CJToken.tMinusEq
                    case CJToken.tStarEq {
                        val kind = switch self.next().get().type {
                            case CJToken.tPlusEq = "+="
                            case CJToken.tMinusEq = "-="
                            case CJToken.tStarEq = "*="
                        }
                        val target = self.expressionToAssignmentTarget(expr)?
                        val valexpr = self.expectExpression()?
                        return Try.Ok(CJAstStatement.AugmentedAssignment(
                            imark, target, kind, valexpr))
                    }
                    default {
                        return Try.Ok(CJAstStatement.Expression(mark, expr))
                    }
                }
            }
        }
    }

    def expectIfStatement(self: Self): Try[CJAstStatement] {
        val mark = self.mark()
        self.expect(CJToken.kwIf)?
        val cond = self.expectExpression()?
        val body = self.expectBlockStatement()?
        val other = (
            if (self.consume(CJToken.kwElse)) Nullable.Some(
                if (self.at(CJToken.kwIf)) self.expectIfStatement()?
                else self.expectBlockStatement()?)
            else Nullable.None)
        return Try.Ok(CJAstStatement.If(mark, cond, body, other))
    }

    def expectBlockStatement(self: Self): Try[CJAstStatement] {
        val mark = self.mark()
        self.expectChar('{')?
        self.skipDelimiters()
        val stmts = List[CJAstStatement].builder()
        while not self.consumeChar('}') {
            stmts.add(self.expectStatement()?)
            self.expectDelimiter()
        }
        return Try.Ok(CJAstStatement.Block(mark, stmts.build()))
    }

    def expectExpression(self: Self): Try[CJAstExpression] {
        return self.expectExpressionWithPrecedence(0)
    }

    def tokenPrecedence(type: Int): Int {
        # Mostly follows Python, except uses Rust style '?'
        return switch type {
            case CJToken.kwOr = 40
            case CJToken.kwAnd = 50
            case '<'.toInt()
            case '>'.toInt()
            case CJToken.tEq
            case CJToken.tNe
            case CJToken.tGe
            case CJToken.tLe
            case CJToken.kwIs
            case CJToken.kwIn
            case CJToken.kwNot = 60
            case '|'.toInt() = 70
            case '^'.toInt() = 80
            case '&'.toInt() = 90
            case CJToken.tLshift
            case CJToken.tRshift
            case CJToken.tRshiftu = 100
            case '+'.toInt()
            case '-'.toInt() = 110
            case '*'.toInt()
            case '/'.toInt()
            case '%'.toInt()
            case CJToken.tTruncdiv = 120
            case CJToken.tPower = 130
            case '.'.toInt()
            case '?'.toInt() = 140
            default = -1
        }
    }

    def expectExpressionWithPrecedence(self: Self, precedence: Int): Try[CJAstExpression] {
        var expr = self.expectAtom()?
        var tokenPrecedence = self.peek().map(t -> Self.tokenPrecedence(t.type)).getOrDefault(-1)
        while tokenPrecedence >= precedence {
            val mark = self.mark()
            switch self.peek().map(t -> t.type).getOrDefault(0) {
                case CJToken.kwOr {
                    self.next()
                    val right = self.expectExpressionWithPrecedence(tokenPrecedence + 1)?
                    expr = CJAstExpression.LogicalOr(mark, expr, right)
                }
                case CJToken.kwAnd {
                    self.next()
                    val right = self.expectExpressionWithPrecedence(tokenPrecedence + 1)?
                    expr = CJAstExpression.LogicalAnd(mark, expr, right)
                }
                case '+'.toInt()
                case '-'.toInt()
                case '*'.toInt()
                case '/'.toInt()
                case '%'.toInt()
                case '<'.toInt()
                case '>'.toInt()
                case '|'.toInt()
                case '^'.toInt()
                case '&'.toInt()
                case CJToken.tLshift
                case CJToken.tRshift
                case CJToken.tRshiftu
                case CJToken.tPower
                case CJToken.tTruncdiv
                case CJToken.tEq
                case CJToken.tNe
                case CJToken.tLe
                case CJToken.tGe
                case CJToken.kwIn
                case CJToken.kwNot {
                    var logicalNot = false
                    var rightAssociative = false
                    var swap = false
                    val methodName = switch self.next().get().type {
                            case '+'.toInt() = "__add"
                            case '-'.toInt() = "__sub"
                            case '*'.toInt() = "__mul"
                            case '/'.toInt() = "__div"
                            case '%'.toInt() = "__rem"
                            case '<'.toInt() = "__lt"
                            case '>'.toInt() = "__gt"
                            case '|'.toInt() = "__or"
                            case '^'.toInt() = "__xor"
                            case '&'.toInt() = "__and"
                            case CJToken.tLshift = "__lshift"
                            case CJToken.tRshift = "__rshift"
                            case CJToken.tRshiftu = "__rshiftu"
                            case CJToken.tPower = "__pow"
                            case CJToken.tTruncdiv = "__truncdiv"
                            case CJToken.tEq = "__eq"
                            case CJToken.tNe = "__eq"
                            case CJToken.tLe = "__le"
                            case CJToken.tGe = "__ge"
                            case CJToken.kwIn = {
                                swap = true
                                "__contains"
                            }
                            case CJToken.kwNot = {
                                self.expect(CJToken.kwIn)?
                                swap = true
                                logicalNot = true
                                "__contains"
                            }
                    }
                    val rhs = self.expectExpressionWithPrecedence(
                        if (rightAssociative) tokenPrecedence else tokenPrecedence + 1)?
                    expr = CJAstExpression.Method(mark, expr, methodName, [rhs])
                }
                case '.'.toInt() {
                    self.next()
                    val methodMark = self.mark()
                    if self.consume(CJToken.kwAwait) {
                        expr = CJAstExpression.Await(methodMark, expr)
                    } else {
                        val name = self.expectId()?
                        if self.atChar('[') or self.atChar('(') {
                            val targs = self.expectTypeArgs()?
                            val args = self.expectArgs()?
                            expr = CJAstExpression.Method(mark, name, targs, args)
                        } else {
                            expr = CJAstExpression.Field(mark, expr, name)
                        }
                    }
                }
                case '?'.toInt() {
                    self.next()
                    expr = CJAstExpression.Propagate(mark, expr)
                }
                default {
                    IO.panic(
                        "TODO: Expression operator " +
                        CJToken.typeToString(self.peek().get().type))
                }
            }
            tokenPrecedence = self.peek().map(t -> Self.tokenPrecedence(t.type)).getOrDefault(-1)
        }
        return Try.Ok(expr)
    }

    def expectAtom(self: Self): Try[CJAstExpression] {
        val mark = self.mark()

        switch self.peek().map(t -> t.type).getOrDefault(0) {
            case CJToken.tId {
                # local variable name or lambda expression
                if self.atLambda() {
                    return self.expectLambdaExpression()
                } else {
                    val name = self.expectId()?
                    return Try.Ok(CJAstExpression.Name(mark, name))
                }
            }
            case CJToken.tInt {
                val rawText = self.next().get().text
                return Try.Ok(CJAstExpression.Int(mark, rawText))
            }
            case CJToken.tDouble {
                val rawText = self.next().get().text
                return Try.Ok(CJAstExpression.Double(mark, rawText))
            }
            case CJToken.tChar {
                val rawText = self.next().get().text
                return Try.Ok(CJAstExpression.Char(mark, rawText))
            }
            case CJToken.tString {
                val rawText = self.next().get().text
                return Try.Ok(CJAstExpression.String(mark, rawText))
            }
            case CJToken.kwTrue {
                self.next()
                return Try.Ok(CJAstExpression.Bool(mark, "true"))
            }
            case CJToken.kwFalse {
                self.next()
                return Try.Ok(CJAstExpression.Bool(mark, "false"))
            }
            case CJToken.kwIf {
                return self.expectIfExpression()
            }
            case '@'.toInt()
            case '['.toInt() {
                return self.expectDisplay()
            }
            case '{'.toInt() {
                return self.expectBlockExpression()
            }
            case CJToken.kwNot {
                self.next()
                val inner = self.expectExpressionWithPrecedence(Self.logicalNotBindingPower)?
                return Try.Ok(CJAstExpression.LogicalNot(mark, inner))
            }
            case CJToken.tTypeId {
                val type = self.expectTypeExpression()?
                self.expectChar('.')?
                var memberMark = self.mark()
                if self.consume(CJToken.kwNew) {
                    val args = self.expectArgs()?
                    return Try.Ok(CJAstExpression.New(memberMark, type, args))
                } else if self.at(CJToken.tTypeId) {
                    val name = self.expectTypeId()?
                    val args = (
                        if (self.atChar('(')) self.expectArgs()?
                        else [])
                    return Try.Ok(CJAstExpression.NewUnion(memberMark, type, name, args))
                } else {
                    val name = self.expectId()?
                    if self.atChar('[') or self.atChar('(') {
                        val targs = self.expectTypeArgs()?
                        val args = self.expectArgs()?
                        return Try.Ok(CJAstExpression.StaticMethod(
                            memberMark, type, name, targs, args))
                    } else {
                        return Try.Ok(CJAstExpression.StaticField(memberMark, type, name))
                    }
                }
            }
            case '+'.toInt()
            case '-'.toInt()
            case '~'.toInt() {
                val methodName = switch self.next().get().type {
                    case '+'.toInt() = "__pos"
                    case '-'.toInt() = "__neg"
                    case '~'.toInt() = "__invert"
                }
                val inner = self.expectExpressionWithPrecedence(Self.defaultUnaryOpBindingPower)?
                return Try.Ok(CJAstExpression.Method(mark, methodName, inner))
            }
            case '('.toInt() {
                if self.atLambda() {
                    return self.expectLambdaExpression()
                } else if self.atOffset(')'.toInt(), 1) {
                    self.next()
                    self.next()
                    return Try.Ok(CJAstExpression.Unit(mark))
                } else {
                    self.next()
                    val expr = self.expectExpression()?
                    if self.consumeChar(',') {
                        val exprs = List[CJAstExpression].builder()
                        exprs.add(expr)
                        while not self.consumeChar(')') {
                            exprs.add(self.expectExpression()?)
                            if not self.consumeChar(',') and not self.atChar(')') {
                                self.expectChar(')')?
                            }
                        }
                        return Try.Ok(CJAstExpression.Tuple(mark, exprs.build()))
                    } else {
                        self.expectChar(')')?
                        return Try.Ok(expr)
                    }
                }
            }
            default {
                return self.failExpected("expression")
            }
        }
    }

    def expectDisplay(self: Self): Try[CJAstExpression] {
        if self.consumeChar('@') {
            val inner = self.expectDisplay()?
            return union inner {
                case List(mark, exprs) = Try.Ok(CJAstExpression.MutableList(mark, exprs))
                case EmptyList(mark, type) = Try.Ok(CJAstExpression.MutableEmptyList(mark, type))
                case Map(mark, pairs) = Try.Ok(CJAstExpression.MutableMap(mark, pairs))
            }
        }
        val mark = self.mark()
        self.expectChar('[')?
        if self.consumeChar(':') {
            self.expectChar(']')?
            return Try.Ok(CJAstExpression.Map(mark, List[Tuple[CJAstExpression, CJAstExpression]].empty()))
        }
        if self.consumeChar(']') {
            return Try.Ok(CJAstExpression.List(mark, List[CJAstExpression].empty()))
        }
        if self.at(CJToken.tTypeId) {
            val save = self.tell()
            val type = self.expectTypeExpression()?
            if self.consumeChar(']') {
                return Try.Ok(CJAstExpression.EmptyList(mark, type))
            }
            self.seek(save)
        }
        val first = self.expectExpression()?
        if self.consumeChar(':') {
            val pairs = List[Tuple[CJAstExpression, CJAstExpression]].builder()
            pairs.add((first, self.expectExpression()?))
            while self.consumeChar(',') and not self.atChar(']') {
                val key = self.expectExpression()?
                self.expectChar(':')?
                val value = self.expectExpression()?
                pairs.add((key, value))
            }
            self.expectChar(']')?
            return Try.Ok(CJAstExpression.Map(mark, pairs.build()))
        }
        val elements = List[CJAstExpression].builder()
        elements.add(first)
        while self.consumeChar(',') and not self.atChar(']') {
            elements.add(self.expectExpression()?)
        }
        self.expectChar(']')?
        return Try.Ok(CJAstExpression.List(mark, elements.build()))
    }

    def expectTypeArgs(self: Self): Try[List[CJAstTypeExpression]] {
        val list = List[CJAstTypeExpression].builder()
        if self.consumeChar('[') {
            while not self.consumeChar(']') {
                list.add(self.expectTypeExpression()?)
                if not self.consumeChar(',') and not self.atChar(']') {
                    self.expectChar(']')
                }
            }
        }
        return Try.Ok(list.build())
    }

    def expectArgs(self: Self): Try[List[CJAstExpression]] {
        self.expectChar('(')?
        val list = List[CJAstExpression].builder()
        while not self.consumeChar(')') {
            list.add(self.expectExpression()?)
            if not self.consumeChar(',') and not self.atChar(')') {
                self.expectChar(')')
            }
        }
        return Try.Ok(list.build())
    }

    def expectBlockExpression(self: Self): Try[CJAstExpression] {
        val mark = self.mark()
        self.expectChar('{')?
        val list = @[CJAstStatement]
        self.skipDelimiters()
        while not self.consumeChar('}') {
            list.add(self.expectStatement()?)
            self.expectDelimiter()?
        }
        val expr = (
            if (list.size() > 0) union list.pop() {
                case Expression(smark, inner) = Nullable.Some(inner)
                default = Nullable.None
            }
            else Nullable.None)
        return Try.Ok(CJAstExpression.Block(mark, list.toListUnsafe(), expr))
    }

    ## Look ahead a few tokens to see if we're currently at a lambda expression
    def atLambda(self: Self): Bool {
        if self.at(CJToken.tId) and self.atOffset(CJToken.tRightArrow, 1) {
            return true
        }
        if not self.atChar('(') {
            return false
        }

        val saved = self.tell()
        self.next()  # skip the first '('
        while self.consume(CJToken.tId) or self.consumeChar(',') {}
        val matched = self.consumeChar(')') and self.consume(CJToken.tRightArrow)
        self.seek(saved)
        return matched
    }

    def expectLambdaExpression(self: Self): Try[CJAstExpression] {
        val mark = self.mark()
        val parameterNames = List[String].builder()
        if self.at(CJToken.tId) {
            parameterNames.add(self.expectId()?)
        } else {
            self.expectChar('(')?
            while not self.consumeChar(')') {
                parameterNames.add(self.expectId()?)
                if not self.consumeChar(',') and not self.atChar(')') {
                    self.expectChar(')')?
                }
            }
        }
        self.expect(CJToken.tRightArrow)?
        val body = (
            if (self.atChar('{')) self.expectBlockStatement()?
            else CJAstStatement.Return(mark, self.expectExpression()?))
        return Try.Ok(CJAstExpression.Lambda(mark, parameterNames, body))
    }

    def expectIfExpression(self: Self): Try[CJAstExpression] {
        val mark = self.mark()
        self.expect(CJToken.kwIf)?
        self.expectChar('(')?
        val cond = self.expectExpression()?
        self.expectChar(')')?
        val body = self.expectExpression()?
        self.expect(CJToken.kwElse)?
        val other = self.expectExpression()?
        return Try.Ok(CJAstExpression.If(mark, cond, body, other))
    }
}
