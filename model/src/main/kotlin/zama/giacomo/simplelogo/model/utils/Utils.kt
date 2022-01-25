package zama.giacomo.simplelogo.model.utils

fun euclideanMod(n: Double, mod: Int) = if (n < 0) (n % mod + mod) % mod else n % mod