package com.sps.tictactoe

import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.badoo.binder.using
import com.badoo.mvicore.android.AndroidBindings
import com.sps.tictactoe.MainActivity.*
import com.sps.tictactoe.TicTacToeFeature.*
import com.sps.tictactoe.TicTacToeFeature.News.*
import com.sps.tictactoe.TicTacToeFeature.State.*
import com.sps.tictactoe.TicTacToeFeature.Wish.*
import com.sps.tictactoe.TicTacToeUiEvent.*
import io.reactivex.functions.Consumer

internal class TicTacToeBindings(
    lifecycleOwner: LifecycleOwner,
    private val feature: TicTacToeFeature,
    private val featureStateToViewModel: ViewModelTransformer,
    private val uiEventToWish: UiEventTransformer,
    //private val newsTransformer: NewsTransformer,
) : AndroidBindings<MainActivity>(lifecycleOwner) {

    object ViewModelTransformer : (State) -> TicTacToeVM {
        override fun invoke(state: State): TicTacToeVM =
            TicTacToeVM(
                board = TicTacToeVM.Board(
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
                gameCounter = TicTacToeVM.GameCounter(
                    xWins = state.gameCounter.xWins,
                    oWins = state.gameCounter.oWins,
                    draws = state.gameCounter.draws,
                )
            )
    }

    object UiEventTransformer : (TicTacToeUiEvent) -> Wish {
        override fun invoke(event: TicTacToeUiEvent): Wish =
            when (event) {
                is CellClicked -> MakeMove(event.index)
                is ResetClicked -> ResetGame
            }
    }

//    object NewsTransformer : (News) -> Unit {
//        override fun invoke(news: News) {
//            when (news) {
//                ResultNews(GameResult.XWINS) -> Toast.makeText(this, "X wins!", Toast.LENGTH_SHORT)
//                    .show()
//                ResultNews(GameResult.OWINS) -> Toast.makeText(this, "O wins", Toast.LENGTH_SHORT)
//                    .show()
//                ResultNews(GameResult.DRAW) -> Toast.makeText(
//                    this,
//                    "It's a draw!",
//                    Toast.LENGTH_SHORT
//                ).show()
//                else -> {}
//            }
//        }
//    }

    override fun setup(view: MainActivity) {
        binder.bind(feature to view using featureStateToViewModel)
        binder.bind(view to feature using uiEventToWish)
        binder.bind(feature.news to Consumer {
            when (it) {
                is ResultNews -> view.showResult(it.result)
            }
        })
    }

}

sealed class TicTacToeUiEvent {
    data class CellClicked(val index: Int) : TicTacToeUiEvent()
    object ResetClicked : TicTacToeUiEvent()
}


