package zama.giacomo.simplelogo.model.logo

import zama.giacomo.simplelogo.model.graphics.Drawing
import zama.giacomo.simplelogo.model.graphics.Line
import zama.giacomo.simplelogo.model.graphics.Point
import zama.giacomo.simplelogo.model.utils.euclideanMod
import kotlin.math.cos
import kotlin.math.sin

data class State(
    val position: Point = Point.Origin,
    val isPenDown: Boolean = true,
    val isTurtleVisible: Boolean = true,
    val direction: Double = 90.0,
    val drawing: Drawing = Drawing.Empty,
    val variables: Map<String, Value<*>> = mapOf()
) {

    fun fd(distance: Int): State {
        val rad = Math.toRadians(direction)
        val destination = Point(
            position.x - distance * cos(rad),
            position.y - distance * sin(rad)
        )
        return if (isPenDown) {
            val line = Line(position, destination)
            copy(
                position = destination,
                drawing = drawing + line
            )
        } else {
            copy(position = destination)
        }
    }

    fun bk(distance: Int): State {
        val rad = Math.toRadians(direction)
        val destination = Point(
            position.x + distance * cos(rad),
            position.y + distance * sin(rad)
        )
        return if (isPenDown) {
            val line = Line(position, destination)
            copy(
                position = destination,
                drawing = drawing + line
            )
        } else {
            copy(position = destination)
        }
    }

    fun rt(angle: Int) = copy(
        direction = euclideanMod(direction + angle, 360)
    )

    fun lt(angle: Int) = copy(
        direction = euclideanMod(direction - angle, 360)
    )

    fun cs() = copy(
        drawing = Drawing.Empty
    )

    fun pu() = copy(
        isPenDown = false
    )

    fun pd() = copy(
        isPenDown = true
    )

    fun ht() = copy(
        isTurtleVisible = false
    )

    fun st() = copy(
        isTurtleVisible = true
    )

    fun home() = copy(
        position = Point.Origin,
        direction = 90.0,
        drawing = drawing + Line(position, Point.Origin)
    )

    fun make(name: String, value: Value<*>) = copy(
        variables = variables + (name to value)
    )

    @Suppress("UNCHECKED_CAST")
    fun <T> deref(name: String): Value<T> {
        val value = variables[name] ?: logoError("Variable $name not declared")
        try {
            return value as Value<T>
        } catch (e: ClassCastException) {
            logoError("Invalid type for variable $name", e)
        }
    }

    fun setxy(x: Int, y: Int): State {
        val destination = Point(x.toDouble(), y.toDouble())
        return if (isPenDown) {
            val line = Line(position, destination)
            copy(
                position = destination,
                drawing = drawing + line
            )
        } else {
            copy(position = destination)
        }
    }
}