package UI

import currentToken
import korlibs.image.color.*

enum class Token(val rgb: RGBA){
    YELLOW(Colors["#f5e614"]),
    RED(Colors["#c42323"]),
    NONE(Colors["#FFFFFF"])
}

fun switchToken(){
    if (currentToken == Token.YELLOW) {
        currentToken = Token.RED
    }
    else if(currentToken == Token.RED) {
        currentToken = Token.YELLOW
    }
    else {
        throw Exception()
    }
}

fun nextToken(): Token {
    if (currentToken == Token.YELLOW) {
        return Token.RED
    }
    else if(currentToken == Token.RED) {
        return Token.YELLOW
    }
    else {
        throw Exception()
    }
}
