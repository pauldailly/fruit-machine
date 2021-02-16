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
        val fruitMachine = FruitMachine(BigDecimal.ONE, BigDecimal.ZERO, SlotGenerator(Random()))

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
        val fruitMachine = FruitMachine(pricePerGame, BigDecimal.ZERO, SlotGenerator(Random()))

        amountsToInsert.forEach {
            fruitMachine.insertMoney(it)
        }

        assertEquals(expectedGames, fruitMachine.gamesRemaining())
    }

    @Test
    fun `pulling fruit machine lever should display the coloured slots generated`() {
        val randomSequence = Stack<Int>()
        randomSequence.addAll(listOf(0, 2, 1, 3))
        val fruitMachine = FruitMachine(BigDecimal.ZERO, BigDecimal.ZERO, SlotGenerator(MockRandom(randomSequence)))

        fruitMachine.pullLever()

        assertEquals(listOf(YELLOW, WHITE, GREEN, BLACK), fruitMachine.slotsDisplayed())
    }

    @Test
    fun `each time the lever is pulled and player doesn't then win their available balance decreases`() {
        val fruitMachine =
            FruitMachine(BigDecimal.ONE, BigDecimal.ZERO, SlotGenerator(MockRandom(nonWinningSequence())))
        fruitMachine.insertMoney(BigDecimal.TEN)

        fruitMachine.pullLever()
        fruitMachine.pullLever()
        fruitMachine.pullLever()

        assertEquals(7, fruitMachine.gamesRemaining())
    }

    @Test
    fun `each time the lever is pulled and player doesn't then win the machine jackpot increases`() {
        val fruitMachine =
            FruitMachine(BigDecimal.ONE, BigDecimal.ZERO, SlotGenerator(MockRandom(nonWinningSequence())))
        fruitMachine.insertMoney(BigDecimal.TEN)

        fruitMachine.pullLever()
        fruitMachine.pullLever()
        fruitMachine.pullLever()

        assertEquals(BigDecimal("3.00"), fruitMachine.currentJackpot())
    }

    @Test
    fun `player will run out of money if they do not win`() {
        val fruitMachine =
            FruitMachine(BigDecimal.ONE, BigDecimal.ZERO, SlotGenerator(MockRandom(nonWinningSequence())))
        fruitMachine.insertMoney(BigDecimal("2.00"))

        fruitMachine.pullLever()
        fruitMachine.pullLever()
        val exception = assertThrows(IllegalStateException::class.java) { fruitMachine.pullLever() }

        assertEquals("You have insufficient credit to play the fruit machine", exception.message)
    }

    @Test
    fun `machine should payout current jackpot if player pulls lever and colours in each slot are the same`() {
        val randomSequence = Stack<Int>()
        randomSequence.addAll(listOf(0, 0, 0, 0, 2, 0, 3, 0, 2, 1, 3, 1))
        val fruitMachine = FruitMachine(BigDecimal.ONE, BigDecimal.ZERO, SlotGenerator(MockRandom(randomSequence)))
        fruitMachine.insertMoney(BigDecimal.TEN)

        fruitMachine.pullLever()
        fruitMachine.pullLever()

        assertEquals(BigDecimal("2.00"), fruitMachine.currentJackpot())
        assertEquals(8, fruitMachine.gamesRemaining())

        fruitMachine.pullLever()

        assertEquals(BigDecimal("0.00"), fruitMachine.currentJackpot())
        assertEquals(10, fruitMachine.gamesRemaining())
        assertEquals(BigDecimal("10.00"), fruitMachine.playerAvailableBalance())
    }

    @Test
    fun `machine should payout half the current money it has when player pulls lever and colours in each slot are all different`() {
        val randomSequence = Stack<Int>()
        randomSequence.addAll(listOf(2, 1, 3, 0))
        val fruitMachine = FruitMachine(BigDecimal.ONE, BigDecimal.TEN, SlotGenerator(MockRandom(randomSequence)))
        fruitMachine.insertMoney(BigDecimal.ONE)

        fruitMachine.pullLever()

        assertEquals(BigDecimal("5.00"), fruitMachine.currentJackpot())
        assertEquals(6, fruitMachine.gamesRemaining())
        assertEquals(BigDecimal("6.00"), fruitMachine.playerAvailableBalance())
    }

    @ParameterizedTest
    @MethodSource("adjacentSlotScenarios")
    fun `machine should payout 5 times cost of single play when player pulls lever and colours in 2 or more adjacent slots are the same`(
        sequence: List<Int>
    ) {
        val randomSequence = Stack<Int>()
        randomSequence.addAll(sequence)
        val fruitMachine = FruitMachine(BigDecimal.ONE, BigDecimal("20.00"), SlotGenerator(MockRandom(randomSequence)))
        fruitMachine.insertMoney(BigDecimal.ONE)

        fruitMachine.pullLever()

        assertEquals(BigDecimal("15.00"), fruitMachine.currentJackpot())
        assertEquals(6, fruitMachine.gamesRemaining())
        assertEquals(BigDecimal("6.00"), fruitMachine.playerAvailableBalance())
    }

    @Test
    fun `when machine does not have sufficient money to pay prize the player should be awarded free plays equal to the amount owed`() {
        val randomSequence = Stack<Int>()
        randomSequence.addAll(listOf(2, 3, 3, 0))
        val fruitMachine = FruitMachine(BigDecimal.ONE, BigDecimal.ONE, SlotGenerator(MockRandom(randomSequence)))
        fruitMachine.insertMoney(BigDecimal.ONE)

        fruitMachine.pullLever()

        assertEquals(BigDecimal("0.00"), fruitMachine.currentJackpot())
        assertEquals(6, fruitMachine.gamesRemaining())
        assertEquals(BigDecimal("2.00"), fruitMachine.playerAvailableBalance())
    }

    @Test
    fun `when machine awards player free plays then player should be able to use them`() {
        val randomSequence = Stack<Int>()
        randomSequence.addAll(nonWinningSequence() + nonWinningSequence() + listOf(2, 3, 3, 0))
        val fruitMachine = FruitMachine(BigDecimal("0.50"), BigDecimal.ZERO, SlotGenerator(MockRandom(randomSequence)))
        fruitMachine.insertMoney(BigDecimal("0.50"))

        fruitMachine.pullLever()

        assertEquals(BigDecimal("0.00"), fruitMachine.currentJackpot())
        assertEquals(6, fruitMachine.gamesRemaining())
        assertEquals(BigDecimal("0.50"), fruitMachine.playerAvailableBalance())

        // Play the 6 games
        fruitMachine.pullLever()
        fruitMachine.pullLever()
        fruitMachine.pullLever()
        fruitMachine.pullLever()
        fruitMachine.pullLever()
        fruitMachine.pullLever()

        // No more money or free plays
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

        @JvmStatic
        fun adjacentSlotScenarios() = listOf(
            Arguments.of(listOf(0, 0, 1, 0)),
            Arguments.of(listOf(0, 0, 0, 1)),
            Arguments.of(listOf(0, 0, 1, 1))
        )
    }

    private fun nonWinningSequence(): Stack<Int> {
        val randomSequence = Stack<Int>()
        randomSequence.addAll(listOf(3, 2, 1, 3, 0, 2, 0, 3, 0, 3, 1, 3, 1, 2, 1, 3))
        return randomSequence
    }
}