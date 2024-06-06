package simulator

data class SimulationResult(
    val iterations: Int,
    val alg1Name: String = "Alg1",
    val alg2Name: String = "Alg2",
    val alg1Wins: Wins,
    val alg2Wins: Wins,
    val draws: Int
)

data class Wins(val asFirstPlayer: Int, val asSecondPlayer: Int)
