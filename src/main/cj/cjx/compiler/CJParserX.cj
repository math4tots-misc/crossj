package cjx.compiler

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
            var repeat = true
            while repeat {
                val trait_ = self.expectTraitExpression()?
                val cond = (
                    if (self.at(CJToken.kwIf)) self.expectTypeConditions()?
                    else [])
                traits.add((trait_, cond))
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
        if self.at(CJToken.kwDef) {
            return Try.Ok(CJAstItemMemberDefinition.Method(
                self.expectMethodDefinition([], comment, modifiers)?))
        } else {
            return Try.Ok(CJAstItemMemberDefinition.Field(
                self.expectFieldDefinition(comment, modifiers)?))
        }
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
        val target = self.expectExtendedAssignmentTarget()?
        return union target {
            case Field(mark, owner, name) = self.failExpectedWithMark("assignment target", mark)
            default = Try.Ok(target)
        }
    }

    def expectExtendedAssignmentTarget(self: Self): Try[CJAstAssignmentTarget] {
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
                    subtargets.add(self.expectExtendedAssignmentTarget()?)
                    if not self.consumeChar(',') and not self.atChar(')') {
                        self.expectChar(')')?
                    }
                }
                return Try.Ok(CJAstAssignmentTarget.Tuple(mark, subtargets.build()))
            }
            default {
                val expr = self.expectExpression()?
                union expr {
                    default {
                        return self.failExpectedWithMark("assignment target", expr.mark())
                    }
                }
            }
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
            case CJToken.kwReturn {
                self.next()
                val expr = self.expectExpression()?
                return Try.Ok(CJAstStatement.Return(mark, expr))
            }

            default {
                val expr = self.expectExpression()?
                return Try.Ok(CJAstStatement.Expression(mark, expr))
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
            self.skipDelimiters()
        }
        return Try.Ok(CJAstStatement.Block(mark, stmts.build()))
    }

    def expectExpression(self: Self): Try[CJAstExpression] {
        return self.expectAtom()
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
            case '('.toInt() {
                if self.atLambda() {
                    return self.expectLambdaExpression()
                } else {
                    val expr = self.expectExpression()?
                    self.expectChar(')')?
                    return Try.Ok(expr)
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
            default {
                return self.failExpected("expression")
            }
        }
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
            if (self.atChar('{')) self.expectStatement()?
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
