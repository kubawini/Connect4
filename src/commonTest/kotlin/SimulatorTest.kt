import logic.HeuristicPlayer
import logic.MonteCarloPlayer
import logic.montecarlo.SimpleMonteCarloAlgorithm
import simulator.Simulator
import kotlin.test.Test

class SimulatorTest {

    @Test
    fun shouldSimulate() {
        val simulator = Simulator(debug = true)
        val alg1 = MonteCarloPlayer(SimpleMonteCarloAlgorithm(printDebugInfo = false))
        val alg2 = HeuristicPlayer()
        val result = simulator.simulate(alg1, alg2, 10)
        println(result)
    }
}