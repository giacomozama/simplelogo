package zama.giacomo.simplelogo.gui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import zama.giacomo.simplelogo.model.logo.Runtime
import zama.giacomo.simplelogo.model.logo.State

class ComposeRuntime(
    private val coroutineScope: CoroutineScope,
    private val logFlow: MutableStateFlow<List<String>>,
    private val channelFlow: MutableStateFlow<State>,
) : Runtime {

    override var shouldContinue = true
        private set

    override fun stop() {
        shouldContinue = false
    }

    override fun print(message: String) {
        coroutineScope.launch {
            logFlow.emit(logFlow.value + message)
        }
    }

    override fun draw(state: State) {
        coroutineScope.launch {
            channelFlow.emit(state)
        }
    }

    override fun finish() {
        shouldContinue = true
    }
}