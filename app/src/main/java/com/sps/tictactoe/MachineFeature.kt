package com.sps.tictactoe

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.BaseFeature
import com.sps.tictactoe.MachineFeature.*
import com.sps.tictactoe.MachineFeature.Effect.*
import com.sps.tictactoe.MachineFeature.Wish.*
import io.reactivex.Observable

class MachineFeature : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(),
    wishToAction = Action::Execute,
    actor = ActorImpl(),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl()
) {

    data class State(
        val index: Int = -1
    )

    sealed class Wish {
        data class StartMachineMove(val board: List<TicTacToeVM.PlayedBy>) : Wish()
    }

    sealed class Action {
        data class Execute(val wish: Wish) : Action()
    }

    sealed class Effect {
        data class MachineNewMove(val index: Int) : Effect()
    }

    sealed class News {
        data class MachineMoveFinished(val index: Int) : News()
    }

    class ActorImpl : Actor<State, Action, Effect> {
        override fun invoke(state: State, action: Action): Observable<Effect> = when (action) {

            is Action.Execute -> when (action.wish) {
                is StartMachineMove ->
                    if ((action.wish.board).all { item -> item != TicTacToeVM.PlayedBy.EMPTY }){
                        Observable.empty()
                    }
                    else Observable.just(MachineNewMove(moveDummy(action.wish.board)))
            }
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

    private class NewsPublisherImpl : NewsPublisher<Action, Effect, State, News> {
        override fun invoke(action: Action, effect: Effect, state: State): News = when (effect) {
            is MachineNewMove -> News.MachineMoveFinished(effect.index)
        }
    }
}