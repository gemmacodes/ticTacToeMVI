package com.sps.tictactoe

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import com.sps.tictactoe.TicTacToeFeature.Effect
import com.sps.tictactoe.TicTacToeFeature.News
import com.sps.tictactoe.TicTacToeFeature.State
import com.sps.tictactoe.TicTacToeFeature.Wish
import io.reactivex.Observable

/**
 * Complete State and feature with required logic..
 * Right now this is extending ActorReducerFeature. Feel Free To change it if you need it.
 * This is just an example, so if you don't need something, feel free to delete it.
 * E.g: You don't need the News? Delete them and pass <Nothing> instead as News Type
 * ActorReducerFeature<Wish, Effect, State, Nothing>
 */

class TicTacToeFeature : ActorReducerFeature<Wish, Effect, State, News>(
    initialState = State(),
    reducer = ReducerImpl(),
    actor = ActorImpl(),
    newsPublisher = NewsPublisherImpl()
) {

    data class State(
        val something: Any? = null
    )

    sealed class Effect {
        object SomeEffect : Effect()
    }

    sealed class Wish {
        object SomeWish : Wish()
    }

    sealed class News {
        object SomeNews : News()
    }

    private class ActorImpl : Actor<State, Wish, Effect> {
        override fun invoke(state: State, action: Wish): Observable<out Effect> {
            return Observable.just(Effect.SomeEffect)
        }

    }

    private class ReducerImpl : Reducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State {
            return State()
        }
    }

    private class NewsPublisherImpl : NewsPublisher<Wish, Effect, State, News> {
        override fun invoke(action: Wish, effect: Effect, state: State): News? {
            return null
        }
    }
}
