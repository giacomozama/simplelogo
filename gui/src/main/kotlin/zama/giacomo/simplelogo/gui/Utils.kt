package zama.giacomo.simplelogo.gui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import zama.giacomo.simplelogo.model.graphics.Point

fun Point.toOffset(canvasSize: Size): Offset {
    val (w, h) = canvasSize
    return Offset(
        w / 2 + x.toFloat(),
        h / 2 + y.toFloat()
    )
}