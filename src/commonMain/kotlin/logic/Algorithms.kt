package logic

import kotlin.random.Random


internal val actions: IntArray = (0 until Width).toList().toIntArray()

internal fun BoardState.generateRandomAction(random: Random, availableActions: IntArray = actions): Int {
    var action = -1
    while (action < 0) {
        val newAction = availableActions.random(random)
        if (canPlay(newAction)) {
            action = newAction
        }
    }
    return action
}
