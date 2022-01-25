package zama.giacomo.simplelogo.parser.visitors

import org.antlr.v4.kotlinruntime.tree.TerminalNode
import zama.giacomo.simplelogo.model.logo.*
import zama.giacomo.simplelogo.parser.LogoBaseVisitor
import zama.giacomo.simplelogo.parser.LogoParser

class CommandVisitor : LogoBaseVisitor<Command>() {

    override fun visitBk(ctx: LogoParser.BkContext): Command {
        val distExprCtx = ctx.findExpression() ?: logoError("Expected expression")
        val distExpr = ExpressionVisitor().visitExpression(distExprCtx)
        return {
            state.bk(distExpr.evaluate(this))
        }
    }

    override fun visitPrint_(ctx: LogoParser.Print_Context): Command {
        val valueCtx = ctx.findValue()
        if (valueCtx != null) {
            val value = ValueVisitor().visitValue(valueCtx)
            return {
                runtime.print(value.value.toString())
                state
            }
        }
        val quotedStringCtx = ctx.findQuotedstring() ?: logoError("Expected value or quoted string")
        val res = StringBuilder()
        fun dfs(quotedstringCtx: LogoParser.QuotedstringContext) {
            for (child in quotedstringCtx.children ?: return) {
                if (child is LogoParser.QuotedstringContext) {
                    dfs(child)
                } else if (child is TerminalNode) {
                    res.append(child.text)
                }
            }
        }
        dfs(quotedStringCtx)
        val str = res.toString().drop(1).dropLast(1)
        return {
            runtime.print(str)
            state
        }
    }

    override fun visitProcedureInvocation(ctx: LogoParser.ProcedureInvocationContext): Command {
        val name = ctx.findName()?.STRING()?.text ?: logoError("Expected procedure name")
        val exprs = mutableListOf<Expression>()
        val expressionVisitor = ExpressionVisitor()
        for (exprCtx in ctx.findExpression()) {
            exprs.add(expressionVisitor.visitExpression(exprCtx))
        }
        return {
            val eArgs = exprs.map { e -> Value.Expr(e).also { it.evaluate(this) } }
            program.executeSubProgram(name, runtime, state, eArgs)
        }
    }

    override fun visitMake(ctx: LogoParser.MakeContext): Command {
        val varName = ctx.STRINGLITERAL()?.text?.drop(1) ?: logoError("Expected variable name")
        val valueCtx = ctx.findValue() ?: logoError("Expected variable value")
        val value = ValueVisitor().visitValue(valueCtx)
        return {
            if (value is Value.Expr && !value.isEvaluated) value.evaluate(this)
            state.make(varName, value)
        }
    }

    override fun visitFd(ctx: LogoParser.FdContext): Command {
        val distExprCtx = ctx.findExpression() ?: logoError("Expected expression.")
        val distExpr = ExpressionVisitor().visitExpression(distExprCtx)
        return {
            state.fd(distExpr.evaluate(this))
        }
    }

    override fun visitRt(ctx: LogoParser.RtContext): Command {
        val angleExprCtx = ctx.findExpression() ?: logoError("Expected expression.")
        val angleExpr = ExpressionVisitor().visitExpression(angleExprCtx)
        return {
            state.rt(angleExpr.evaluate(this))
        }
    }

    override fun visitLt(ctx: LogoParser.LtContext): Command {
        val angleExprCtx = ctx.findExpression() ?: logoError("Expected expression.")
        val angleExpr = ExpressionVisitor().visitExpression(angleExprCtx)
        return {
            state.lt(angleExpr.evaluate(this))
        }
    }

    override fun visitCs(ctx: LogoParser.CsContext): Command = {
        state.cs()
    }

    override fun visitPu(ctx: LogoParser.PuContext): Command = {
        state.pu()
    }

    override fun visitPd(ctx: LogoParser.PdContext): Command = {
        state.pd()
    }

    override fun visitHt(ctx: LogoParser.HtContext): Command = {
        state.ht()
    }

    override fun visitSt(ctx: LogoParser.StContext): Command = {
        state.st()
    }

    override fun visitHome(ctx: LogoParser.HomeContext): Command = {
        state.home()
    }

    override fun visitStop(ctx: LogoParser.StopContext): Command = {
        runtime.stop()
        state
    }

    override fun visitSetxy(ctx: LogoParser.SetxyContext): Command {
        val exprCtxs = ctx.findExpression()
        val expressionVisitor = ExpressionVisitor()
        val xExprCtx = exprCtxs.getOrElse(0) { logoError("Expected expression") }
        val xExpr = expressionVisitor.visitExpression(xExprCtx)
        val yExprCtx = exprCtxs.getOrElse(1) { logoError("Expected expression") }
        val yExpr = expressionVisitor.visitExpression(yExprCtx)
        return {
            state.setxy(
                xExpr.evaluate(this),
                yExpr.evaluate(this),
            )
        }
    }

    override fun visitIfe(ctx: LogoParser.IfeContext): Command {
        val comparisonCtx = ctx.findComparison() ?: logoError("Expected condition")
        val comparandCtxs = comparisonCtx.findExpression()
        if (comparandCtxs.size != 2) logoError("Expected exactly 2 comparands")
        val expressionVisitor = ExpressionVisitor()
        val (a,b) = comparandCtxs.map { expressionVisitor.visitExpression(it) }
        val blockCtx = ctx.findBlock() ?: logoError("Expected block")
        val block = BlockVisitor().visitBlock(blockCtx)

        fun Context.executeBlock(): State {
            var st = state
            for (cmd in block) st = cmd.invoke(Context(runtime, program, st, args))
            return st
        }

        return when (comparisonCtx.findComparisonOperator()?.text ?: logoError("Expected comparison operator")) {
            "<" -> {
                {
                    if (a.evaluate(this) < b.evaluate(this)) executeBlock() else state
                }
            }
            ">" -> {
                {
                    if (a.evaluate(this) > b.evaluate(this)) executeBlock() else state
                }
            }
            "=" -> {
                {
                    if (a.evaluate(this) == b.evaluate(this)) executeBlock() else state
                }
            }
            else -> {
                logoError("Invalid comparison operator")
            }
        }
    }

    override fun visitRepeat_(ctx: LogoParser.Repeat_Context): Command {
        val repetitions = ctx.findNumber()?.NUMBER()?.text?.toIntOrNull()
            ?: logoError("Expected number of repetitions")
        val block = ctx.findBlock() ?: logoError("Expected block")
        val blockVisitor = BlockVisitor()
        val commands = blockVisitor.visitBlock(block)
        return {
            var st = state
            repeat(repetitions) {
                for (cmd in commands) {
                    st = cmd.invoke(Context(runtime, program, st, args))
                }
            }
            st
        }
    }

    override fun visitFore(ctx: LogoParser.ForeContext): Command {
        val varName = ctx.findName()?.STRING()?.text ?: logoError("Expected iteration variable name")

        val controlExprCtxs = ctx.findExpression()
        if (controlExprCtxs.size != 3) logoError("Expected exactly 3 control expressions")
        val expressionVisitor = ExpressionVisitor()
        val start = expressionVisitor.visitExpression(controlExprCtxs[0])
        val end = expressionVisitor.visitExpression(controlExprCtxs[1])
        val step = expressionVisitor.visitExpression(controlExprCtxs[2])

        val block = ctx.findBlock() ?: logoError("Expected block")
        val blockVisitor = BlockVisitor()

        val commands = blockVisitor.visitBlock(block)
        return {
            var st = state
            for (i in start.evaluate(this)..end.evaluate(this) step step.evaluate(this)) {
                for (cmd in commands) {
                    st = cmd.invoke(
                        Context(
                            runtime = runtime,
                            program = program,
                            state = st,
                            args = args + (varName to Value.Integer(i))
                        )
                    )
                }
            }
            st
        }
    }
}