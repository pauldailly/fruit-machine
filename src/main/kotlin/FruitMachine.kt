import java.math.BigDecimal
import java.math.RoundingMode

class FruitMachine(
    private val pricePerGame: BigDecimal,
    initialBalance: BigDecimal,
    private val slotGenerator: SlotGenerator
) {

    private var playerBalance = BigDecimal.ZERO
    private var machineBalance = initialBalance
    private var slots = emptyList<FruitMachineColour>()
    private var freeGames = 0

    fun pullLever() {
        if (playerHasInsufficientCredit()) {
            throw IllegalStateException("You have insufficient credit to play the fruit machine")
        }

        slots = slotGenerator.generatedSlots()
        when {
            allSlotsAreSameColour() -> {
                calculateIdenticalSlotsPrize()
            }
            allSlotsAreDifferentColours() -> {
                calculateUniqueSlotsPrize()
            }
            adjacentSlotsAreSameColour() -> {
                calculateAdjacentSlotPrize()
            }
            else -> {
                playerPaysForGame()
            }
        }
    }

    fun slotsDisplayed() = slots

    fun insertMoney(amountInserted: BigDecimal) {
        playerBalance += amountInserted
    }

    fun gamesRemaining(): Int = playerBalance.divide(pricePerGame, RoundingMode.FLOOR).toInt() + freeGames
    fun currentJackpot(): BigDecimal = machineBalance.setScale(2)
    fun playerAvailableBalance(): BigDecimal = playerBalance.setScale(2)
    private fun calculateIdenticalSlotsPrize() {
        playerBalance += machineBalance
        machineBalance = BigDecimal.ZERO
    }

    private fun calculateUniqueSlotsPrize() {
        val prize = machineBalance.divide(BigDecimal("2"), 2, RoundingMode.HALF_UP)
        machineBalance -= prize
        playerBalance += prize
    }

    private fun calculateAdjacentSlotPrize() {
        val prize = pricePerGame.multiply(BigDecimal("5"))
        if (prize <= machineBalance) {
            machineBalance -= prize
            playerBalance += prize
        } else {
            playerBalance += machineBalance
            freeGames = (prize.subtract(machineBalance)).divide(pricePerGame, RoundingMode.FLOOR).toInt()
            machineBalance = BigDecimal.ZERO
        }
    }

    private fun allSlotsAreDifferentColours() = slots.distinct().size == 4

    private fun allSlotsAreSameColour() = slots.distinct().size == 1

    private fun adjacentSlotsAreSameColour(): Boolean {
        // look at first slot, if it's same as second then stop
        // else look at second slot, if same as 3rd
        var i = 0
        while (i < slots.size - 1) {
            if (slots[i] == slots[i + 1]) {
                return true
            }
            ++i
        }
        return false
    }

    private fun playerPaysForGame() {
        if (freeGames > 0) {
            --freeGames
        } else {
            machineBalance += pricePerGame
            playerBalance -= pricePerGame
        }
    }

    private fun playerHasInsufficientCredit() = playerBalance < pricePerGame && freeGames == 0
}