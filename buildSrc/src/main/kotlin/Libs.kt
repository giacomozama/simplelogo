object Libs {

    object AntlrKotlin {
        private const val version = "160bc0b70f"
        private const val groupName = "com.strumenta.antlr-kotlin"
        const val runtime = "$groupName:antlr-kotlin-runtime-jvm:$version"
        const val target = "$groupName:antlr-kotlin-target:$version"
        const val plugin = "$groupName:antlr-kotlin-gradle-plugin:$version"
    }
}