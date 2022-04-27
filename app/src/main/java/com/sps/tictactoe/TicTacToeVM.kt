package com.sps.tictactoe

import com.sps.tictactoe.TicTacToeVM.PlayingState.*

/**
 * This is a suggested TicTacToeVM.
 * You can change it to fits your needs.
 * You will have to change the view if you change the vm.
 */

data class TicTacToeVM (
    val board: Board = Board(),
    val gameCounter: GameCounter = GameCounter(),
) {
    /**
     *   |c1|c2|c3|
     *   |c4|c5|c6|
     *   |c7|c8|c9|
     */

    data class Board(
        val c1: PlayingState = EMPTY, //Index 0
        val c2: PlayingState = EMPTY, //Index 1
        val c3: PlayingState = EMPTY, //Index 2
        val c4: PlayingState = EMPTY, //Index 3
        val c5: PlayingState = EMPTY, //Index 4
        val c6: PlayingState = EMPTY, //Index 5
        val c7: PlayingState = EMPTY, //Index 6
        val c8: PlayingState = EMPTY, //Index 7
        val c9: PlayingState = EMPTY, //Index 8
    ) {
        fun asCellList() = listOf(c1, c2, c3, c4, c5, c6, c7, c8, c9)
    }

    enum class PlayingState {
        EMPTY, X, O;
    }

    data class GameCounter(
        val xWins: Int = 0,
        val oWins: Int = 0,
        val draws: Int = 0,
    )
}
