import FruitMachineColour.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.*

internal class FruitMachineTest {

    @Test
    fun `cannot pull fruit machine lever before player has inserted money`() {
        val fruitMachine = FruitMachine(BigDecimal.ONE, SlotGenerator(Random()))

        val exception = assertThrows(IllegalStateException::class.java) { fruitMachine.pullLever() }

        assertEquals("You have insufficient credit to play the fruit machine", exception.message)
    }

    @ParameterizedTest
    @MethodSource("moneyInsertScenarios")
    fun `adding money to the machine will increase the number of games a player has remaining`(
        amountsToInsert: List<BigDecimal>,
        expectedGames: Int
    ) {
        val pricePerGame = BigDecimal("1.00")
        val fruitMachine = FruitMachine(pricePerGame, SlotGenerator(Random()))

        amountsToInsert.forEach {
            fruitMachine.insertMoney(it)
        }

        assertEquals(expectedGames, fruitMachine.gamesRemaining())
    }

    @Test
    fun `pulling fruit machine lever should display the coloured slots generated`() {
        val randomSequence = Stack<Int>()
        randomSequence.addAll(listOf(0,2,1,3))
        val fruitMachine = FruitMachine(BigDecimal.ZERO, SlotGenerator(MockRandom(randomSequence)))

        fruitMachine.pullLever()

        assertEquals(listOf(YELLOW, WHITE, GREEN, BLACK), fruitMachine.slotsDisplayed())
    }

    @Test
    fun `each time the lever is pulled and player doesn't then win their available balance decreases`() {
        val randomSequence = Stack<Int>()
        randomSequence.addAll(listOf(0,2,1,3,0,2,1,3,0,2,1,3,0,2,1,3))
        val fruitMachine = FruitMachine(BigDecimal.ONE, SlotGenerator(MockRandom(randomSequence)))
        fruitMachine.insertMoney(BigDecimal.TEN)

        fruitMachine.pullLever()
        fruitMachine.pullLever()
        fruitMachine.pullLever()

        assertEquals(7, fruitMachine.gamesRemaining())
    }

    @Test
    fun `each time the lever is pulled and player doesn't then win the machine jackpot increases`() {
        val randomSequence = Stack<Int>()
        randomSequence.addAll(listOf(0,2,1,3,0,2,1,3,0,2,1,3,0,2,1,3))
        val fruitMachine = FruitMachine(BigDecimal.ONE, SlotGenerator(MockRandom(randomSequence)))
        fruitMachine.insertMoney(BigDecimal.TEN)

        fruitMachine.pullLever()
        fruitMachine.pullLever()
        fruitMachine.pullLever()

        assertEquals(BigDecimal("3.00"), fruitMachine.currentJackpot())
    }

    @Test
    fun `player will run out of money if they do not win`() {
        val randomSequence = Stack<Int>()
        randomSequence.addAll(listOf(0,2,1,3,0,2,1,3,0,2,1,3,0,2,1,3))
        val fruitMachine = FruitMachine(BigDecimal.ONE, SlotGenerator(MockRandom(randomSequence)))
        fruitMachine.insertMoney(BigDecimal("2.00"))

        fruitMachine.pullLever()
        fruitMachine.pullLever()
        val exception = assertThrows(IllegalStateException::class.java) { fruitMachine.pullLever() }

        assertEquals("You have insufficient credit to play the fruit machine", exception.message)
    }

    companion object {
        @JvmStatic
        fun moneyInsertScenarios() = listOf(
            Arguments.of(emptyList<BigDecimal>(), 0),
            Arguments.of(listOf(BigDecimal("0.99")), 0),
            Arguments.of(listOf(BigDecimal("1.00")), 1),
            Arguments.of(listOf(BigDecimal("1.01")), 1),
            Arguments.of(listOf(BigDecimal("0.50"), BigDecimal("0.50")), 1),
            Arguments.of(listOf(BigDecimal("1.50"), BigDecimal("1.50")), 3)
        )
    }

}