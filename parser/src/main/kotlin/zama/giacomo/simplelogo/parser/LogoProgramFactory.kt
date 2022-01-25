package zama.giacomo.simplelogo.parser

import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import zama.giacomo.simplelogo.model.logo.Program
import zama.giacomo.simplelogo.parser.visitors.ProgramVisitor


class LogoProgramFactory {

    fun create(code: String): Program {
        val lexer = LogoLexer(CharStreams.fromString(code))
        val parser = LogoParser(CommonTokenStream(lexer))
        val programVisitor = ProgramVisitor()
        val progContext = parser.prog()
        val program = programVisitor.visitProg(progContext)
        return program
    }
}