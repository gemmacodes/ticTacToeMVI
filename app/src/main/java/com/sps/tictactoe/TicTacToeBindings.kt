package com.sps.tictactoe

import androidx.lifecycle.LifecycleOwner
import com.badoo.binder.using
import com.badoo.mvicore.android.AndroidBindings
import com.sps.tictactoe.TicTacToeFeature.*
import com.sps.tictactoe.TicTacToeFeature.News.*
import com.sps.tictactoe.TicTacToeFeature.Wish.*
import com.sps.tictactoe.TicTacToeUiEvent.*
import io.reactivex.functions.Consumer

internal class TicTacToeBindings(
    lifecycleOwner: LifecycleOwner,
    private val feature: TicTacToeFeature,
    private val featureStateToViewModel: ViewModelTransformer,
    private val uiEventToWish: UiEventTransformer,
) : AndroidBindings<MainActivity>(lifecycleOwner) {

    object ViewModelTransformer : (State) -> TicTacToeVM {
        override fun invoke(state: State): TicTacToeVM =
            TicTacToeVM(
                board = TicTacToeVM.Board(
                    c1 = state.boardAsList[0],
                    c2 = state.boardAsList[1],
                    c3 = state.boardAsList[2],
                    c4 = state.boardAsList[3],
                    c5 = state.boardAsList[4],
                    c6 = state.boardAsList[5],
                    c7 = state.boardAsList[6],
                    c8 = state.boardAsList[7],
                    c9 = state.boardAsList[8],
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
                is CellClicked -> HumanMove(event.index)
                is ResetClicked -> ResetGame
            }
    }


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


