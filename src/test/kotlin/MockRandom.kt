import java.util.*

class MockRandom(private val desiredSequence: Stack<Int>) : Random() {

    override fun nextInt(bound: Int): Int {
        return desiredSequence.pop()
    }
}