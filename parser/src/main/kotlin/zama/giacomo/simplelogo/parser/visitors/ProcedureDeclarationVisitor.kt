package zama.giacomo.simplelogo.parser.visitors

import zama.giacomo.simplelogo.model.logo.Command
import zama.giacomo.simplelogo.model.logo.Program
import zama.giacomo.simplelogo.model.logo.logoError
import zama.giacomo.simplelogo.parser.LogoBaseVisitor
import zama.giacomo.simplelogo.parser.LogoParser

class ProcedureDeclarationVisitor: LogoBaseVisitor<Program>() {

    override fun visitProcedureDeclaration(ctx: LogoParser.ProcedureDeclarationContext): Program {
        val name = ctx.findName()?.STRING()?.text ?: logoError("Expected procedure name")

        val argNames = mutableListOf<String>()
        fun dfs(ctx: LogoParser.ParameterDeclarationsContext) {
            val n = ctx.findName()?.STRING()?.text ?: logoError("Expected parameter name")
            argNames.add(n)
            for (c in ctx.findParameterDeclarations()) dfs(c)
        }
        for (c in ctx.findParameterDeclarations()) dfs(c)

        val subPrograms = mutableMapOf<String, Program>()
        val commands = mutableListOf<Command>()
        val commandVisitor = CommandVisitor()
        for (line in ctx.findLine()) {
            val pdCtx = line.findProcedureDeclaration()
            if (pdCtx != null) {
                val proc = visitProcedureDeclaration(pdCtx)
                if (subPrograms.put(proc.name, proc) != null) {
                    logoError("Procedure with name ${proc.name} already defined")
                }
                continue
            }
            val printCtx = line.findPrint_()
            if (printCtx != null) {
                commands.add(commandVisitor.visitPrint_(printCtx))
                continue
            }
            for (cmdCtx in line.findCmd()) {
                commands.add(commandVisitor.visitCmd(cmdCtx))
            }
        }

        return Program(name, argNames, subPrograms, commands)
    }
}