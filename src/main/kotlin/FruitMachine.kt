import java.math.BigDecimal
import java.math.RoundingMode

class FruitMachine(val pricePerGame: BigDecimal, val slotGenerator: SlotGenerator) {

    private var playerBalance = BigDecimal.ZERO
    private var machineBalance = BigDecimal.ZERO
    private var slots = emptyList<FruitMachineColour>()

    fun pullLever() {
        if (playerBalance < pricePerGame) {
            throw IllegalStateException("You have insufficient credit to play the fruit machine")
        }
        slots = slotGenerator.generatedSlots()
        if (slots.distinct().size == 1) {
            playerBalance += machineBalance
            machineBalance = BigDecimal.ZERO
        } else {

            machineBalance += pricePerGame
            playerBalance -= pricePerGame
        }
    }

    fun slotsDisplayed() = slots

    fun insertMoney(amountInserted: BigDecimal) {
        playerBalance += amountInserted
    }

    fun gamesRemaining(): Int = playerBalance.divide(pricePerGame, RoundingMode.FLOOR).toInt()
    fun currentJackpot(): BigDecimal = machineBalance.setScale(2)

}