import FruitMachineColour.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*

internal class SlotGeneratorTest {

    @ParameterizedTest
    @MethodSource("generatedSlotScenarios")
    fun `should return list of slot colours based on random generator`(
        sequence: List<Int>,
        expectedSlotColours: List<FruitMachineColour>
    ) {
        val sequenceStack = Stack<Int>()
        sequenceStack.addAll(sequence.reversed())
        val generator = SlotGenerator(MockRandom(sequenceStack))

        assertEquals(expectedSlotColours, generator.generatedSlots())
    }

    companion object {
        @JvmStatic
        fun generatedSlotScenarios() = listOf(
            Arguments.of(
                listOf(0, 0, 0, 0), listOf(BLACK, BLACK, BLACK, BLACK),
            ),
            Arguments.of(listOf(1, 1, 1, 1), listOf(WHITE, WHITE, WHITE, WHITE)),
            Arguments.of(
                listOf(0, 1, 0, 1), listOf(BLACK, WHITE, BLACK, WHITE)
            ),
            Arguments.of(
                listOf(0, 1, 2, 3), listOf(BLACK, WHITE, GREEN, YELLOW),
            )
        )
    }


    private class MockRandom(val desiredSequence: Stack<Int>) : Random() {

        override fun nextInt(bound: Int): Int {
            return desiredSequence.pop()
        }
    }
}