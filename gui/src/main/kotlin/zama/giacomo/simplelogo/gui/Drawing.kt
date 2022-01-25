package zama.giacomo.simplelogo.gui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawOutline
import zama.giacomo.simplelogo.model.graphics.Point
import zama.giacomo.simplelogo.model.logo.State
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun Drawing(modifier: Modifier, state: State) {
    val drawing = state.drawing
    val direction = state.direction

    Canvas(modifier = modifier) {
        for (line in drawing.lines) {
            drawLine(
                color = Color.Black,
                start = line.from.toOffset(size),
                end = line.to.toOffset(size)
            )
        }

        if (state.isTurtleVisible) {
            drawOutline(
                outline = turtleOutline(state.position, direction, size),
                color = Color(0x808000A0)
            )
        }
    }
}

private fun turtleOutline(position: Point, direction: Double, canvasSize: Size): Outline {
    // here we draw the turtle, an equilater triangle
    // pointing up rotated by (direction - 90) degrees
    val pos = position.toOffset(canvasSize)
    val h = -20f * (sqrt(3f) / 2f)
    val rot = direction - 90.0
    val rad = Math.toRadians(rot)
    val x1 = (-h * sin(rad)).toFloat()
    val y1 = (h * cos(rad)).toFloat()
    val x2 = (10f * cos(rad)).toFloat()
    val y2 = (10f * sin(rad)).toFloat()
    val x3 = (-10f * cos(rad)).toFloat()
    val y3 = (-10f * sin(rad)).toFloat()
    return Outline.Generic(
        Path().apply {
            moveTo(pos.x + x1, pos.y + y1)
            lineTo(pos.x + x2, pos.y + y2)
            lineTo(pos.x + x3, pos.y + y3)
            close()
        }
    )
}