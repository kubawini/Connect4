package logic

class DummyPlayer : GamePlayer {
    override fun chooseColumnToPlay(gameState: BoardState): Int {
        for (i in 0..6){
            if(gameState.numberOfTokens(i) < 6)
                return i
        }
        return -1
    }

    override fun reset() {

    }
}
