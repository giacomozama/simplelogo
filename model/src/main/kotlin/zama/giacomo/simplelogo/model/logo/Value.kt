package zama.giacomo.simplelogo.model.logo

interface Value<T> {

    fun get(): T

    class Str(private val value: String) : Value<String> {
        override fun get() = value
    }

    class Integer(private val value: Int) : Value<Int> {
        override fun get() = value
    }

    class Expr(private val expression: Expression) : Value<Int> {
        var isEvaluated = false

        private var value: Int? = null

        override fun get() = value!!

        fun evaluate(context: Context) {
            if (!isEvaluated) value = expression.evaluate(context)
            isEvaluated = true
        }
    }

    class Deref<T>(private val varName: String) : Value<T> {
        var isEvaluated = false

        private var value: T? = null

        override fun get() = value!!

        fun evaluate(context: Context) {
            if (!isEvaluated) value = context.deref(varName)
            isEvaluated = true
        }
    }
}