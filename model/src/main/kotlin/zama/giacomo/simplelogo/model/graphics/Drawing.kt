package zama.giacomo.simplelogo.model.graphics

class Drawing private constructor(
    val lines: List<Line>
) {

    operator fun plus(line: Line) = Drawing(
        lines = lines + line
    )

    companion object {
        val Empty = Drawing(emptyList())
    }
}