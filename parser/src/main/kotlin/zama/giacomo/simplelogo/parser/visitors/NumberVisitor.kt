package zama.giacomo.simplelogo.parser.visitors

import zama.giacomo.simplelogo.model.logo.logoError
import zama.giacomo.simplelogo.parser.LogoBaseVisitor
import zama.giacomo.simplelogo.parser.LogoParser

class NumberVisitor : LogoBaseVisitor<Int>() {

    override fun visitNumber(ctx: LogoParser.NumberContext): Int {
        return ctx.NUMBER()?.text?.toIntOrNull() ?: logoError("Expected valid number")
    }
}