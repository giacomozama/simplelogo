package zama.giacomo.simplelogo.model.logo

class Program(
    val name: String,
    private val argNames: List<String>,
    private val subPrograms: Map<String, Program>,
    private val commands: List<Command>
) {
    private var parent: Program? = null

    init {
        for (sp in subPrograms.values) sp.parent = this
    }

    private fun getSubProgram(name: String): Program = subPrograms[name]
        ?: parent?.getSubProgram(name)
        ?: logoError("Unknown procedure: $name")

    fun executeSubProgram(
        name: String,
        runtime: Runtime,
        state: State,
        args: List<Value<*>>
    ) = getSubProgram(name).execute(runtime, state, args)

    private fun execute(
        runtime: Runtime,
        state: State,
        args: List<Value<*>>
    ): State {
        if (argNames.size != args.size) {
            logoError(
                "Procedure $name invocation: expected ${argNames.size} parameters, got ${args.size}"
            )
        }
        val argsMap = argNames.zip(args).toMap()
        var st = state
        for (cmd in commands) {
            if (!runtime.shouldContinue) break
            st = cmd.invoke(Context(runtime, this, st, argsMap))
        }
        return st
    }

    fun execute(
        runtime: Runtime,
        state: State
    ): State {
        val st = execute(runtime, state, emptyList())
        runtime.draw(st)
        runtime.finish()
        return st
    }
}