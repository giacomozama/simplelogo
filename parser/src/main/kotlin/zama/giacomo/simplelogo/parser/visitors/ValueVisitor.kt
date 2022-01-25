package zama.giacomo.simplelogo.parser.visitors

import zama.giacomo.simplelogo.model.logo.Value
import zama.giacomo.simplelogo.model.logo.logoError
import zama.giacomo.simplelogo.parser.LogoBaseVisitor
import zama.giacomo.simplelogo.parser.LogoParser

class ValueVisitor : LogoBaseVisitor<Value<*>>() {

    override fun visitValue(ctx: LogoParser.ValueContext): Value<*> {
        ctx.STRINGLITERAL()?.text?.drop(1)?.let { return Value.Str(it) }
        ctx.findDeref()?.let { return visitDeref(it) }
        ctx.findExpression()?.let { return visitExpression(it) } ?: logoError("Expected expression")
    }

    override fun visitExpression(ctx: LogoParser.ExpressionContext): Value<*> {
        return Value.Expr(ExpressionVisitor().visitExpression(ctx))
    }

    override fun visitDeref(ctx: LogoParser.DerefContext): Value<*> {
        return Value.Deref<Any>(ctx.findName()?.STRING()?.text ?: logoError("Expected variable name"))
    }
}