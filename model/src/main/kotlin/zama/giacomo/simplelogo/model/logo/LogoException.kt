package zama.giacomo.simplelogo.model.logo

class LogoException(message: String, cause: Throwable?) : Exception(message, cause) {
    constructor(message: String) : this(message, null)
}

fun logoError(message: String): Nothing = throw LogoException(message)

fun logoError(message: String, cause: Throwable): Nothing = throw LogoException(message, cause)