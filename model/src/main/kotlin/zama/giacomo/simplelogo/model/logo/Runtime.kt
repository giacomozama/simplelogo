package zama.giacomo.simplelogo.model.logo

interface Runtime {

    fun stop()

    val shouldContinue: Boolean

    fun print(message: String)

    fun draw(state: State)

    fun finish()
}