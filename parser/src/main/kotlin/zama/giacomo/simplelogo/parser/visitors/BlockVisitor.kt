package zama.giacomo.simplelogo.parser.visitors

import zama.giacomo.simplelogo.model.logo.Command
import zama.giacomo.simplelogo.parser.LogoBaseVisitor
import zama.giacomo.simplelogo.parser.LogoParser

class BlockVisitor : LogoBaseVisitor<List<Command>>() {

    override fun visitBlock(ctx: LogoParser.BlockContext): List<Command> {
        val commandVisitor = CommandVisitor()
        return ctx.findCmd().map { commandVisitor.visitCmd(it) }
    }
}