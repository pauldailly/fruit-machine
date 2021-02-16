import java.math.BigDecimal
import java.math.RoundingMode

class FruitMachine(private val pricePerGame: BigDecimal, initialBalance: BigDecimal, private val slotGenerator: SlotGenerator) {

    private var playerBalance = BigDecimal.ZERO
    private var machineBalance = initialBalance
    private var slots = emptyList<FruitMachineColour>()

    fun pullLever() {
        if (playerBalance < pricePerGame) {
            throw IllegalStateException("You have insufficient credit to play the fruit machine")
        }
        slots = slotGenerator.generatedSlots()
        if (slots.distinct().size == 1) {
            playerBalance += machineBalance
            machineBalance = BigDecimal.ZERO
        } else if (slots.distinct().size == 4) {
            val prize = machineBalance.divide(BigDecimal("2"), 2, RoundingMode.HALF_UP)
            machineBalance -= prize
            playerBalance += prize
        } else if(adjacentSlotsAreSameColour(slots)) {
            val prize = pricePerGame.multiply(BigDecimal("5"))
            machineBalance -= prize
            playerBalance += prize
        }
        else {
            machineBalance += pricePerGame
            playerBalance -= pricePerGame
        }
    }

    private fun adjacentSlotsAreSameColour(slots: List<FruitMachineColour>): Boolean {
        // look at first slot, if it's same as second then stop
        // else look at second slot, if same as 3rd
        var i = 0
        while (i < slots.size - 1){
            if(slots[i] == slots[i+1]) {
                return true
            }
            ++i
        }
        return false
    }

    fun slotsDisplayed() = slots

    fun insertMoney(amountInserted: BigDecimal) {
        playerBalance += amountInserted
    }

    fun gamesRemaining(): Int = playerBalance.divide(pricePerGame, RoundingMode.FLOOR).toInt()
    fun currentJackpot(): BigDecimal = machineBalance.setScale(2)
    fun playerAvailableBalance(): BigDecimal = playerBalance.setScale(2)

}