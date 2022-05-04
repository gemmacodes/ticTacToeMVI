package com.sps.tictactoe

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import com.sps.tictactoe.DummyFeature.*
import com.sps.tictactoe.DummyFeature.Effect.*
import com.sps.tictactoe.DummyFeature.Wish.*
import io.reactivex.Observable

class DummyFeature : ActorReducerFeature<Wish, Effect, State, Nothing>(
    initialState = State(),
    actor = ActorImpl(),
    reducer = ReducerImpl(),
) {

    data class State(
        val index : Int = -1,
        val dummyMoveFinished: Boolean = false
    )

    sealed class Wish {
        data class StartMachineMove(val board: List<TicTacToeVM.PlayedBy>) : Wish()
    }

    sealed class Effect {
        data class MachineNewMove(val index: Int) : Effect()
        object ResetEffect : Effect()
    }

    class ActorImpl : Actor<State, Wish, Effect> {
        override fun invoke(state: State, wish: Wish): Observable<Effect> = when (wish) {
            is StartMachineMove -> Observable.merge(
                Observable.just(MachineNewMove(moveDummy(wish.board))),
                Observable.just(ResetEffect)
            )
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
            is MachineNewMove -> state.copy(index = effect.index, dummyMoveFinished = true)
            is ResetEffect -> state.copy(dummyMoveFinished = false)
        }
    }

}
