import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import zama.giacomo.simplelogo.gui.ComposeRuntime
import zama.giacomo.simplelogo.gui.Drawing
import zama.giacomo.simplelogo.gui.StandardRoundedCornerShape
import zama.giacomo.simplelogo.model.logo.State
import zama.giacomo.simplelogo.parser.LogoProgramFactory

@ExperimentalMaterialApi
@Composable
@Preview
fun App() {
    val coroutineScope = rememberCoroutineScope()
    val runtime = ComposeRuntime(coroutineScope, logFlow, stateFlow)
    val logState = logFlow.collectAsState()
    val stateState = stateFlow.collectAsState()

    var error by remember { mutableStateOf("") }
    val log by remember { logState }
    val state by remember { stateState }
    var code by remember { mutableStateOf("") }

    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            if (error.isNotEmpty()) {
                AlertDialog(
                    text = {
                        Text(error)
                    },
                    buttons = {
                    },
                    onDismissRequest = {
                        error = ""
                    }
                )
            }
            Drawing(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(StandardRoundedCornerShape)
                    .background(
                        color = Color(0xFFEEEEEE),
                        shape = StandardRoundedCornerShape
                    ),
                state = state
            )
            Spacer(Modifier.padding(8.dp))
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
            ) {
                Text(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF222222),
                            shape = StandardRoundedCornerShape
                        )
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                        .weight(1f),
                    text = log.joinToString("\n"),
                    color = Color.White
                )
                Spacer(Modifier.height(16.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f)
                        .clip(
                            shape = StandardRoundedCornerShape
                        ),
                    value = code,
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace
                    ),
                    maxLines = 100,
                    onValueChange = { code = it }
                )
                Spacer(Modifier.height(8.dp))
                Row() {
                    Button(
                        modifier = Modifier.weight(1f),
                        shape = StandardRoundedCornerShape,
                        onClick = {
                            try {
                                LogoProgramFactory().create(code + "\n").execute(runtime, state)
                            } catch (e: Exception) {
                                error = e.message ?: "ERROR"
                            }
                        }) {
                        Text("Execute")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        shape = StandardRoundedCornerShape,
                        onClick = {
                            try {
                                code = ""
                            } catch (e: Exception) {
                                error = e.message ?: "ERROR"
                            }
                        }) {
                        Text("Clear")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        shape = StandardRoundedCornerShape,
                        onClick = {
                            try {
                                coroutineScope.launch {
                                    stateFlow.emit(State())
                                    logFlow.emit(emptyList())
                                }
                            } catch (e: Exception) {
                                error = e.message ?: "ERROR"
                            }
                        }
                    ) {
                        Text("Reset")
                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
fun main() = application {
    Window(
        title = "SimpleLogo",
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}

val logFlow = MutableStateFlow(emptyList<String>())
val stateFlow = MutableStateFlow(State())