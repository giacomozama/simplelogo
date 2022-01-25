package zama.giacomo.simplelogo.parser.visitors

import zama.giacomo.simplelogo.model.logo.Command
import zama.giacomo.simplelogo.model.logo.Program
import zama.giacomo.simplelogo.model.logo.logoError
import zama.giacomo.simplelogo.parser.LogoBaseVisitor
import zama.giacomo.simplelogo.parser.LogoParser

class ProgramVisitor : LogoBaseVisitor<Program>() {

    override fun visitProg(ctx: LogoParser.ProgContext): Program {
        val subPrograms = mutableMapOf<String, Program>()
        val commands = mutableListOf<Command>()
        val commandVisitor = CommandVisitor()
        val procedureDeclarationVisitor = ProcedureDeclarationVisitor()
        for (line in ctx.findLine()) {
            val pdCtx = line.findProcedureDeclaration()
            if (pdCtx != null) {
                val proc = procedureDeclarationVisitor.visitProcedureDeclaration(pdCtx)
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
        return Program("MAIN", emptyList(), subPrograms, commands)
    }
}