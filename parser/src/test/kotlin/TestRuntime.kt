import zama.giacomo.simplelogo.model.logo.Runtime
import zama.giacomo.simplelogo.model.logo.State

class TestRuntime(
    private val onPrint: (String) -> Unit = {},
    private val onDraw: (State) -> Unit = {}
) : Runtime {

    override var shouldContinue = true
        private set

    override fun stop() {
        shouldContinue = false
    }

    override fun print(message: String) {
        onPrint(message)
    }

    override fun draw(state: State) {
        onDraw(state)
    }

    override fun finish() {
        shouldContinue = true
    }
}