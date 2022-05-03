package com.sps.tictactoe.twoFeatures

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import com.sps.tictactoe.TicTacToeVM
import com.sps.tictactoe.twoFeatures.DummyFeature.*
import com.sps.tictactoe.twoFeatures.DummyFeature.Effect.*
import com.sps.tictactoe.twoFeatures.DummyFeature.Wish.*
import io.reactivex.Observable

class DummyFeature : ActorReducerFeature<Wish, Effect, State, Nothing>(
    initialState = State(),
    actor = ActorImpl(),
    reducer = ReducerImpl(),
) {

    data class State(
        val index : Int = -1
    )

    sealed class Wish {
        data class StartMachineMove(val board: List<TicTacToeVM.PlayedBy>) : Wish()
    }

    sealed class Effect {
        data class MachineNewMove(val index: Int, val board:List<TicTacToeVM.PlayedBy>) : Effect()
    }

    class ActorImpl : Actor<State, Wish, Effect> {
        override fun invoke(state: State, wish: Wish): Observable<Effect> = when (wish) {
            is StartMachineMove -> Observable.just(MachineNewMove(moveDummy(wish.board), wish.board))
        }

        private fun moveDummy(board: List<TicTacToeVM.PlayedBy>): Int {
            val emptyCells = mutableListOf<Int>()
            board.forEachIndexed { i, element ->
                if (element == TicTacToeVM.PlayedBy.EMPTY) emptyCells.add(i)
            }

            return emptyCells.random()
        }
    }

    class ReducerImpl : Reducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State = when (effect) {
            is MachineNewMove -> state.copy(index = effect.index)
        }
    }

}
