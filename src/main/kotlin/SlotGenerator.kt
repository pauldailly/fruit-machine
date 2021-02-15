import java.util.*

class SlotGenerator(private val random : Random) {

    companion object {
        private val size = FruitMachineColour.values().size
    }

    fun generatedSlots() : List<FruitMachineColour> {
        val slots = mutableListOf<FruitMachineColour>()

        repeat(4) {
            slots.add(FruitMachineColour.values()[random.nextInt(size)])
        }

        return slots
    }

}