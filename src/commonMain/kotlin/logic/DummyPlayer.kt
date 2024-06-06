package logic

class DummyPlayer : GamePlayer {
    override fun chooseColumnToPlay(gameState: BoardState): Int {
        val matrix = gameState.toMatrix()
        for(array in matrix.reversed()) {
            for (j in array) {
                print("$j ")
            }
            println()
        }
        println()
        for (i in 0..6){
            if(gameState.numberOfTokens(i) < 6)
                return i
        }
        return -1
    }
}
