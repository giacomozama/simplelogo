package zama.giacomo.simplelogo.parser.visitors

import org.antlr.v4.kotlinruntime.tree.TerminalNode
import zama.giacomo.simplelogo.model.logo.Expression
import zama.giacomo.simplelogo.model.logo.logoError
import zama.giacomo.simplelogo.parser.LogoBaseVisitor
import zama.giacomo.simplelogo.parser.LogoParser

class ExpressionVisitor : LogoBaseVisitor<Expression>() {

    override fun visitDeref(ctx: LogoParser.DerefContext): Expression {
        val varName = ctx.findName()?.STRING()?.text ?: logoError("Expected variable name")
        return Expression.Deref(varName)
    }

    override fun visitRandom(ctx: LogoParser.RandomContext): Expression {
        val expressionCtx = ctx.findExpression() ?: logoError("Expected expression")
        val expr = visitExpression(expressionCtx)
        return Expression.Random(expr)
    }

    override fun visitSignExpression(ctx: LogoParser.SignExpressionContext): Expression {
        var sign = 1
        for (child in ctx.children ?: logoError("Expected expression")) {
            when (child) {
                is LogoParser.NumberContext -> {
                    val number = NumberVisitor().visitNumber(child)
                    return if (sign == 1) {
                        Expression.UnaryPlus(Expression.Immediate(number))
                    } else {
                        Expression.UnaryMinus(Expression.Immediate(number))
                    }
                }
                is LogoParser.DerefContext -> {
                    return if (sign == 1) {
                        Expression.UnaryPlus(visitDeref(child))
                    } else {
                        Expression.UnaryMinus(visitDeref(child))
                    }
                }
                is LogoParser.Func_Context -> {
                    return if (sign == 1) {
                        Expression.UnaryPlus(visitFunc_(child))
                    } else {
                        Expression.UnaryMinus(visitFunc_(child))
                    }
                }
                is TerminalNode -> {
                    sign *= when (child.text) {
                        "-" -> -1
                        else -> 1
                    }
                }
            }
        }
        logoError("Expected expression")
    }

    override fun visitMultiplyingExpression(ctx: LogoParser.MultiplyingExpressionContext): Expression {
        var expectingSignExpr = true
        var operator: String? = null
        var curExpr: Expression? = null
        for (child in ctx.children ?: logoError("Invalid expression")) {
            when (child) {
                is LogoParser.SignExpressionContext -> {
                    if (!expectingSignExpr) logoError("Expected operator")
                    expectingSignExpr = false
                    val expr = visitSignExpression(child)
                    curExpr = when (operator) {
                        "*" -> {
                            Expression.Multiplication(curExpr!!, expr)
                        }
                        "/" -> {
                            Expression.Division(curExpr!!, expr)
                        }
                        null -> {
                            expr
                        }
                        else -> {
                            logoError("Invalid token $operator")
                        }
                    }
                }
                is TerminalNode -> {
                    if (expectingSignExpr) logoError("Expected operand")
                    operator = child.text
                    expectingSignExpr = true
                }
            }
        }
        if (expectingSignExpr) logoError("Expected operand")
        return curExpr ?: logoError("Expected expression")
    }

    override fun visitExpression(ctx: LogoParser.ExpressionContext): Expression {
        var expectingMultiplyingExpr = true
        var operator: String? = null
        var curExpr: Expression? = null
        for (child in ctx.children ?: logoError("Invalid expression")) {
            when (child) {
                is LogoParser.MultiplyingExpressionContext -> {
                    if (!expectingMultiplyingExpr) logoError("Expected operator")
                    expectingMultiplyingExpr = false
                    val expr = visitMultiplyingExpression(child)
                    curExpr = when (operator) {
                        "+" -> {
                            Expression.Sum(curExpr!!, expr)
                        }
                        "-" -> {
                            Expression.Difference(curExpr!!, expr)
                        }
                        null -> {
                            expr
                        }
                        else -> {
                            logoError("Invalid token $operator")
                        }
                    }
                }
                is TerminalNode -> {
                    if (expectingMultiplyingExpr) logoError("Expected operand")
                    operator = child.text
                    expectingMultiplyingExpr = true
                }
            }
        }
        if (expectingMultiplyingExpr) logoError("Expected operand")
        return curExpr ?: logoError("Expected expression")
    }
}