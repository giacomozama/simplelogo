package zama.giacomo.simplelogo.model.logo

class Context(
    val runtime: Runtime,
    val program: Program,
    val state: State,
    val args: Map<String, Value<*>>
) {

    @Suppress("UNCHECKED_CAST")
    fun <T> deref(name: String): T {
        val value = args[name] ?: state.deref<T>(name)
        try {
            return (value as Value<T>).value
        } catch (e: ClassCastException) {
            logoError("Invalid type for variable $name", e)
        }
    }

    val <T> Value<T>.value: T get() {
        if (this is Value.Expr && !isEvaluated) evaluate(this@Context)
        if (this is Value.Deref<T> && !isEvaluated) evaluate(this@Context)
        return get()
    }
}