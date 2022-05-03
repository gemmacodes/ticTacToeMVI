package com.sps.tictactoe

import com.sps.tictactoe.TicTacToeVM.*

class DummyPlayer (private val board: List<PlayedBy>){

    fun moveDummy(): Int{
        val emptyCells = mutableListOf<Int>()
        board.forEachIndexed { i, element ->
            if (element == PlayedBy.EMPTY) emptyCells.add(i)
        }

        return emptyCells.random()
    }

}
