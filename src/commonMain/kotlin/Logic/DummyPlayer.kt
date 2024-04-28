package Logic

class DummyPlayer : MonteCarloPlayer {
    override fun chooseColumnToPlay(board: Board): Int {
        for (i in 0..6){
            if(board.numberOfTokens(i) < 6)
                return i
        }
        return -1
    }
}
