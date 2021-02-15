import java.math.BigDecimal
import java.math.RoundingMode

class FruitMachine(val pricePerGame: BigDecimal) {

    private var playerBalance = BigDecimal.ZERO
    private val slots = emptyList<FruitMachineColour>()

    fun pullLever() {
        throw IllegalStateException("You have insufficient credit to play the fruit machine")
    }

    fun insertMoney(amountInserted: BigDecimal) {
        playerBalance += amountInserted
    }

    fun gamesRemaining(): Int = playerBalance.divide(pricePerGame, RoundingMode.FLOOR).toInt()

}