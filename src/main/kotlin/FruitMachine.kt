import java.math.BigDecimal
import java.math.RoundingMode

class FruitMachine(val pricePerGame: BigDecimal, val slotGenerator: SlotGenerator) {

    private var playerBalance = BigDecimal.ZERO
    private var slots = emptyList<FruitMachineColour>()

    fun pullLever() {
        if(playerBalance < pricePerGame){
            throw IllegalStateException("You have insufficient credit to play the fruit machine")
        }

        slots = slotGenerator.generatedSlots()
    }

    fun slotsDisplayed() = slots

    fun insertMoney(amountInserted: BigDecimal) {
        playerBalance += amountInserted
    }

    fun gamesRemaining(): Int = playerBalance.divide(pricePerGame, RoundingMode.FLOOR).toInt()

}