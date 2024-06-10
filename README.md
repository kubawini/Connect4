# Connect4

## About project
Connect 4 game using MCTS. The game allows 2 playing modes
- human vs human,
- human vs AI.

There have been multiplied several versions of MCTS algorithms:
- basic MCTS algorithm,
- MCTS with transposition tables,
- MCTS with Last-Good-Reply (LGR) modification,
- MCTS hybrid version (transposition tables + LGR),
- Heuristic approach.

The GUI allowing human to play has been implemented using Kotlin language and Korge framework. Whole logic as well as AI rivalry simulator have been implemented using Kotlin as well.

![image](https://github.com/kubawini/Connect4/assets/93740269/b4f24c6f-f4e7-43bd-a87c-fecf9f570939)

Read more about project in our [summary](./Raport_MCTS.pdf) (in Polish).

## Algorithms
### Basic MCTS algorithm
It is a probabilistic algorithm that selects the path that returns the best results on average
(relative to the selected metric). The algorithm is often used to select moves in games. In general
concept, MCTS searches the state space of a game represented by a tree to find the
the best move. MCTS finds the best decision by balancing exploration and exploitation within a
limited budget, e.g. running time. The MCTS algorithm searches the state space of the game
iteratively. After a sufficient number of iterations, the algorithm returns the best action found
for state S using the formula:

MCTS searches game states space iteratively. After completing sufficient nymber of iterations, algorithm returns the best possible actios for state $S$ according to formula:

$a^* = arg\max_{a\in{A(S)}}{Q(S,a)},$

The basic MCTS algorithm consists of four phases:
- selections,
- expansion,
- simulation,
- back propagation.

Each phase has been explained in more details in our [summary](./Raport_MCTS.pdf)

### MCTS with transposition tables
Transposition tables address the issue that while searching the game tree, an algorithm may reach the same state through different sequences of actions. In the basic version of the MCTS algorithm, such states would be independent. This approach causes the algorithm to potentially re-evaluate the same state multiple times during its execution, slowing it down. Transposition tables are tables that contain information about visited states in the game. In the MCTS algorithm, this information is the average result $Q(S)$ for a given state $S$. It is used in the action selection phase $a^*$ according to the formula:

$a^*=\arg \max _{a \in A(S)}\left\{Q(g(S,a))+C \sqrt{\frac{\ln [N(S)]}{N(s, a)}}\right\},$

where $g(S,a)$ is the state reached after performing action $a$ in state $S$.

The transposition table is updated during the backpropagation phase.
In this project, the transposition table was implemented as a simple hash table with a limited size.

### MCTS with Last-Good-Reply modification
The LGR (Last Good Reply) method assumes that actions which led to victory in a certain state should also be good in other states. After the simulation phase, information about actions—replies to the opponent's actions that led to victory in a given simulation—is recorded. Subsequent simulations can use this recorded information, preferring actions that were previously good replies to the opponent's actions. If during a simulation an action, recorded as a good reply, is not legal in a given state, a random action from the available ones is chosen.

The method can be extended to the LGR-n method, which involves recording information not only about the reply to a single action but also about replies to sequences of actions of length n. In such a case, the longest match method is used during the action selection phase in simulations, choosing an action that was previously a good reply to the last k actions performed.

### Heuristic approach
The strategic moves are based on the strategy outlined in a video by [Keith Gallia](https://www.youtube.com/watch?v=YqqcNjQMX18) posted on
on YouTube. Strategic move generation is based on the following rules:
1. when the opponent makes two moves side by side on the bottom row block the formation of a three,
2. start with the middle column and play the middle column until it reaches a height of 5,
3. as player one try to place 3 chips next to each other in odd rows counting from the
bottom,
4. as player two try to place 3 chips next to each other in even rows counting from the bottom

Translated with DeepL.com (free version)

## Results
The MCTS algorithm performs well in the connect 4 game, and its great potential for modification is an
interesting aspect for finding better and better algorithms for two-player games. It turned out that the MCTS modification with
transposition tables is not significantly better than the basic version, but the LGR modification is
significantly better, which was confirmed by statistical tests.

![image](https://github.com/kubawini/Connect4/assets/93740269/32f1146a-c6dc-4559-9d0e-42e4dbf2dee2)

To read detailed description check our [summary](./Raport_MCTS.pdf).
