package com.sps.tictactoe

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.BaseFeature
import com.sps.tictactoe.HumanFeature.*
import com.sps.tictactoe.HumanFeature.Effect.*
import com.sps.tictactoe.HumanFeature.Wish.*
import com.sps.tictactoe.TicTacToeVM.PlayedBy.*
import io.reactivex.Observable

class HumanFeature : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(),
    reducer = ReducerImpl(),
    wishToAction = Action::Execute,
    actor = ActorImpl(),
    newsPublisher = NewsPublisherImpl(),
) {

    data class State(
        val index: Int = -1
    )

    sealed class Wish {
        data class StartHumanMove(val board: List<TicTacToeVM.PlayedBy>, val index: Int) : Wish()
    }

    sealed class Action {
        data class Execute(val wish: Wish) : Action()
    }

    sealed class Effect {
        data class HumanNewMove(val index: Int) : Effect()
    }

    sealed class News {
        data class HumanMoveFinished(val index: Int) : News()
    }

    private class ActorImpl : Actor<State, Action, Effect> {

        override fun invoke(state: State, action: Action): Observable<out Effect> {
            return when (action) {

                is Action.Execute -> when (action.wish) {
                    is StartHumanMove -> {
                        makeHumanMove(action.wish)
                    }
                }
            }
        }

        private fun makeHumanMove(wish: StartHumanMove): Observable<out Effect> {
            return if (wish.board[wish.index] == EMPTY) {
                Observable.just(HumanNewMove(index = wish.index))
            } else {
                Observable.empty()
            }
        }

    }


    private class ReducerImpl : Reducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State {
            return when (effect) {
                is HumanNewMove -> state.copy(index = effect.index)
            }
        }
    }


    private class NewsPublisherImpl : NewsPublisher<Action, Effect, State, News> {
        override fun invoke(action: Action, effect: Effect, state: State): News = when (effect) {
            is HumanNewMove -> News.HumanMoveFinished(effect.index)
        }
    }

}

