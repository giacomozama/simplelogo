import org.junit.jupiter.api.Test
import zama.giacomo.simplelogo.model.logo.State
import zama.giacomo.simplelogo.parser.LogoProgramFactory
import kotlin.test.assertTrue


// some examples from https://github.com/antlr/grammars-v4/tree/master/logo/logo/examples
class LogoParserTest {

    @Test
    fun example1_triangle() {
        LogoProgramFactory()
            .create("fd 60 rt 120 fd 60 rt 120 fd 60 rt 120")
            .execute(TestRuntime(), State())
    }

    @Test
    fun example2_triangle() {
        LogoProgramFactory()
            .create("cs pu setxy -60 60 pd home rt 45 fd 85 lt 135 fd 120")
            .execute(TestRuntime(), State())
    }

    @Test
    fun example3_print_Ada_Lovelace() {
        LogoProgramFactory().create(
            """
                make "first_programmer "Ada_Lovelace
                print :first_programmer
            """.trimIndent()
        ).execute(
            TestRuntime(onPrint = {
                assertTrue {
                    it == "Ada_Lovelace"
                }
            }
            ),
            State()
        )
    }

    @Test
    fun example4_circle() {
        LogoProgramFactory().create(
            """
                make "angle 0
                repeat 1000 [fd 3 rt :angle make "angle :angle + 7]
            """.trimIndent()
        ).execute(TestRuntime(), State())
    }

    @Test
    fun fractal() {
        LogoProgramFactory().create(
            """
                to triangle :size
                  to fractal :size
                    repeat 3 [fd :size rt 120]
                    if :size < 3 [stop] ; the procedure stops if size is too small
                  end
                  repeat 3 [fd :size fractal :size/2 fd :size rt 120]
                end
                
                triangle 100
            """.trimIndent()
        ).execute(TestRuntime(), State())
    }

    @Test
    fun expression() {
        val log = mutableListOf<String>()
        LogoProgramFactory().create(
            """
                make "size 81/9
                print 2*3
                print :size - 4
            """.trimIndent()
        ).execute(
            TestRuntime(
                onPrint = log::add
            ),
            state = State()
        )
        assertTrue { log[0] == "6" && log[1] == "5" }
    }

    @Test
    fun flower() {
        LogoProgramFactory()
            .create("repeat 8 [rt 45 repeat 6 [repeat 90 [fd 1 rt 2] rt 90]] ht")
            .execute(TestRuntime(), State())
    }

    @Test
    fun house() {
        LogoProgramFactory().create(
            """
                to square :size
                	repeat 4 [fd :size rt 90]
                end
                	
                to floor :size
                	repeat 2 [fd :size rt 90 fd :size * 2 rt 90]
                end

                to house
                	floor 60 fd 60 floor 60
                	pu fd 20 rt 90 fd 20 lt 90 pd
                	square 20

                	pu rt 90 fd 60 lt 90 pd
                	square 20
                end
                
                house
            """.trimIndent()
        ).execute(TestRuntime(), State())
    }
}