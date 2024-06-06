import logic.HeuristicPlayer
import logic.MonteCarloPlayer
import logic.montecarlo.SimpleMonteCarloAlgorithm
import simulator.Simulator
import kotlin.test.Test

class SimulatorTest {

    @Test
    fun shouldSimulate() {
        val simulator = Simulator(debug = true)
        val alg1Factory = {
            MonteCarloPlayer(SimpleMonteCarloAlgorithm(printDebugInfo = false))
        }
        val alg2Factory = {
            HeuristicPlayer()
        }
        val result = simulator.simulate(alg1Factory, alg2Factory, 10)
        println(result)
    }
}