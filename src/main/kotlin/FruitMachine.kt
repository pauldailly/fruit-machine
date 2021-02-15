import java.math.BigDecimal
import java.math.RoundingMode

class FruitMachine(val pricePerGame: BigDecimal, val slotGenerator: SlotGenerator) {

    private var playerBalance = BigDecimal.ZERO
    private var machineBalance = BigDecimal.ZERO
    private var slots = emptyList<FruitMachineColour>()

    fun pullLever() {
        if(playerBalance < pricePerGame){
            throw IllegalStateException("You have insufficient credit to play the fruit machine")
        }
        machineBalance+=pricePerGame
        playerBalance-=pricePerGame
        slots = slotGenerator.generatedSlots()
    }

    fun slotsDisplayed() = slots

    fun insertMoney(amountInserted: BigDecimal) {
        playerBalance += amountInserted
    }

    fun gamesRemaining(): Int = playerBalance.divide(pricePerGame, RoundingMode.FLOOR).toInt()
    fun currentJackpot(): BigDecimal = machineBalance.setScale(2)

}