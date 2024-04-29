import Logic.DummyPlayer
import UI.*
import korlibs.event.*
import korlibs.korge.*
import korlibs.korge.view.*
import korlibs.image.color.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.input.*
import korlibs.korge.style.*
import korlibs.korge.ui.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*

var cellSize: Float = 0f
var radius: Float = 0f
var fieldLeftIndent: Float = 0f
var fieldTopIndent: Float = 0f
var fieldWidth: Float = 0f
var fieldHeight: Float = 0f
var leftIndent: Float = 0f
var topIndent: Float = 0f
var columnIndent: Float = 0f
var columnHeight: Float = 0f
var currentToken: Token = Token.YELLOW
var rowBreak: Float = 0f
var board: Logic.Board = Logic.Board()
var gameOver: Boolean = false
var freezeWindow: Boolean = false
val windowWidth: Float = 660f
val windowHeight: Float = 665f
var singlePlayerMode: Boolean = false // TODO if you want to play single player, change to true
var animationPending: Boolean = false
var aiPlaying = false
var boardVM: UI.Board = UI.Board(null, DummyPlayer())


suspend fun main() = Korge(windowSize = Size(windowWidth, windowHeight), backgroundColor = Token.NONE.rgb) {

    cellSize = views.virtualWidth / 8f
    rowBreak = 4f
    radius = cellSize / 2 - rowBreak
    fieldLeftIndent = 5f
    fieldTopIndent = 5f
    fieldWidth = 2 * fieldLeftIndent + 7 * cellSize
    fieldHeight = 2 * fieldTopIndent + 6 * cellSize
    leftIndent = (views.virtualWidth - fieldWidth) / 2
    topIndent = 140f
    columnIndent = topIndent - cellSize
    columnHeight = fieldHeight + cellSize
    val menuWidth = views.virtualWidth
    val menuHeight = 40

    boardVM = board(this)

    val menu = solidRect(Size(menuWidth, menuHeight), Colors["#F3F3F3"]){
        position(0,0)
    }

    val restartButton = container {
        val background = roundRect(Size(30,30), RectCorners(5f), fill = RGBA(0,0,0,0))
        alignTopToTopOf(menu, 10.0)
        alignRightToRightOf(menu, 10.0)
        image(resourcesVfs["refresh.png"].readBitmap()) {
            size(background.width * 0.8, background.height * 0.8)
            centerOn(background)
        }
        onClick {
            if(!freezeWindow) {
                boardVM.restart()
            }
        }
    }

    val undoButton = container {
        val background = roundRect(Size(30,30), RectCorners(5f), fill = RGBA(0,0,0,0))
        alignTopToTopOf(menu, 10.0)
        alignRightToLeftOf(restartButton, 10.0)
        image(resourcesVfs["back.png"].readBitmap()) {
            size(background.width * 0.8, background.height * 0.8)
            centerOn(background)
        }
    }

    val closeButton = container {
        val background = roundRect(Size(30,30), RectCorners(5f), fill = RGBA(0,0,0,0))
        alignTopToTopOf(menu, 10.0)
        alignLeftToLeftOf(menu, 10.0)
        image(resourcesVfs["close.png"].readBitmap()) {
            size(background.width * 0.8, background.height * 0.8)
            centerOn(background)
        }
    }

}

fun Stage.showPopup(board: UI.Board){
    if(gameOver and !freezeWindow) {
        freezeWindow = true
        showGameOver {
            board.restart()
            gameOver = false
            freezeWindow = false
            animationPending = false
            aiPlaying = false
        }
    }
}

fun Container.showGameOver(onRestart: () -> Unit) = container {
    fun restart() {
        this@container.removeFromParent()
        onRestart()
    }

    position(leftIndent, topIndent)

    var infoText = ""
    if (currentToken == Token.RED){
        infoText = "YELLOW won"
    }
    else{
        infoText = "RED won"
    }

    roundRect(Size(fieldWidth, fieldHeight), RectCorners(5), fill = Colors["#FFFFFF33"])
    text(infoText, 60f, Colors.BLACK) {
        centerBetween(0f, 0f, fieldWidth, fieldHeight)
        y -= 60
    }
    uiText("Play again", Size(120.0, 35.0)) {
        centerBetween(0f, 0f, fieldWidth, fieldHeight)
        y += 20
        styles.textSize = 40.0
        styles.textColor = RGBA(0, 0, 0)
        onOver { styles.textColor = RGBA(90, 90, 90) }
        onOut { styles.textColor = RGBA(0, 0, 0) }
        onDown { styles.textColor = RGBA(120, 120, 120) }
        onUp { styles.textColor = RGBA(120, 120, 120) }
        onClick { restart() }
    }

    keys.down {
        when (it.key) {
            Key.ENTER, Key.SPACE -> restart()
            else -> Unit
        }
    }
}
