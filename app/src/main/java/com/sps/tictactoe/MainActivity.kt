package com.sps.tictactoe

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava2.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sps.tictactoe.MainActivity.ViewModel.PlayingState.EMPTY
import com.sps.tictactoe.composables.GameBoard
import com.sps.tictactoe.composables.GameCounter
import com.sps.tictactoe.composables.ResetButton
import com.sps.tictactoe.ui.theme.TicTacToeTheme
import io.reactivex.Observable
import io.reactivex.ObservableSource


class MainActivity : ComponentActivity() {

    /**
     * This is a suggested viewmodel.
     * You can change it to fits your needs.
     * You will have to change the view if you change the vm.
     */
    data class ViewModel(
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

    private val featureState = TicTacToeFeature()
    private val viewModelObservable = featureState.wrapToObservable().map(::mapViewModel)

//    private val newsObservable = featureState.news.wrapToObservable().subscribe {
//        //TODO: Handle your news here
//        when(it){
//            TicTacToeFeature.News.ResultNews -> Toast.makeText(this, "News Received", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun mapViewModel(state: TicTacToeFeature.State): ViewModel {
        //map your state to the expectedViewModel here
        return ViewModel(
            board = ViewModel.Board(
                c1 = state.cellList[0],
                c2 = state.cellList[1],
                c3 = state.cellList[2],
                c4 = state.cellList[3],
                c5 = state.cellList[4],
                c6 = state.cellList[5],
                c7 = state.cellList[6],
                c8 = state.cellList[7],
                c9 = state.cellList[8],
            ), 
            gameCounter = ViewModel.GameCounter(
                xWins = state.gameCounter.xWins,
                oWins = state.gameCounter.oWins,
                draws = state.gameCounter.draws,
            )

        )
    }

    private fun onResetClicked() {
        // invoke whatever you need here to reset the feature
        featureState.accept(TicTacToeFeature.Wish.ResetGame)
    }

    private fun onCellClicked(cellIndex: Int) {
        // invoke whatever you need here to place a piece in the board
        featureState.accept(TicTacToeFeature.Wish.MakeMove(cellIndex))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val boardState = viewModelObservable.map { it.board }
                        .subscribeAsState(ViewModel.Board())
//                    val boardState = remember {
//                        mutableStateOf(ViewModel.Board())
//                    }

                    val counterState =
                        viewModelObservable.map { it.gameCounter }
                            .subscribeAsState(ViewModel.GameCounter())

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GameBoard(board = boardState.value) {
                            onCellClicked(it)
                        }
                        ResetButton(::onResetClicked)
                        GameCounter(gameCounter = counterState.value)
                    }

                }
            }
        }
    }


    private fun <T> ObservableSource<out T>.wrapToObservable(): Observable<T> =
        Observable.wrap(cast())

    private inline fun <reified T> Any?.cast(): T = this as T

}






