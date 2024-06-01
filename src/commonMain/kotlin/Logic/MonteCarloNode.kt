package Logic

class MonteCarloNode(private var visits: Int, private var scoresSum: Int) {
    val avgScore: Double = scoresSum / visits.toDouble()
    fun updateScore(score: Int) {
        visits++
        scoresSum += score
    }
}