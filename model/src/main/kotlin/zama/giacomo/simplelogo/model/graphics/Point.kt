package zama.giacomo.simplelogo.model.graphics

data class Point(val x: Double, val y: Double) {

    companion object {
        val Origin = Point(0.0, 0.0)
    }
}