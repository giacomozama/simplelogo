package zama.giacomo.simplelogo.model.logo


sealed interface Expression {

    fun evaluate(context: Context): Int

    class Sum(
        private val a: Expression,
        private val b: Expression
    ): Expression {
        override fun evaluate(context: Context) = a.evaluate(context) + b.evaluate(context)
    }

    class Difference(
        private val a: Expression,
        private val b: Expression
    ): Expression {
        override fun evaluate(context: Context) = a.evaluate(context) - b.evaluate(context)
    }

    class Multiplication(
        private val a: Expression,
        private val b: Expression
    ): Expression {
        override fun evaluate(context: Context) = a.evaluate(context) * b.evaluate(context)
    }

    class Division(
        private val a: Expression,
        private val b: Expression
    ): Expression {
        override fun evaluate(context: Context) = a.evaluate(context) / b.evaluate(context)
    }

    class UnaryPlus(
        private val a: Expression
    ): Expression {
        override fun evaluate(context: Context) = a.evaluate(context)
    }

    class UnaryMinus(
        private val a: Expression
    ): Expression {
        override fun evaluate(context: Context) = -a.evaluate(context)
    }

    class Immediate(
        private val a: Int
    ): Expression {
        override fun evaluate(context: Context) = a
    }

    class Deref(
        private val varName: String
    ): Expression {
        override fun evaluate(context: Context): Int = context.deref(varName)
    }

    class Random(
        private val until: Expression
    ): Expression {
        override fun evaluate(context: Context) = kotlin.random.Random.nextInt(until.evaluate(context))
    }
}
