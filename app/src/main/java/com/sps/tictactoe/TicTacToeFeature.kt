package com.sps.tictactoe

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.PostProcessor
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.BaseFeature
import com.sps.tictactoe.TicTacToeFeature.*
import com.sps.tictactoe.TicTacToeFeature.Effect.*
import com.sps.tictactoe.TicTacToeFeature.State.GameStatus.*
import com.sps.tictactoe.TicTacToeFeature.Wish.MakeMove
import com.sps.tictactoe.TicTacToeFeature.Wish.ResetGame
import com.sps.tictactoe.TicTacToeVM.GameCounter
import com.sps.tictactoe.TicTacToeVM.PlayedBy
import com.sps.tictactoe.TicTacToeVM.PlayedBy.*
import io.reactivex.Observable


class TicTacToeFeature : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(),
    reducer = ReducerImpl(),
    postProcessor = PostProcessorImpl(),
    wishToAction = Action::Execute,
    actor = ActorImpl(),
    newsPublisher = NewsPublisherImpl(),
) {

    data class State(
        val boardAsList: List<PlayedBy> = listOf(
            EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY
        ),
        val gameStatus: GameStatus = READY,
        val currentPlayer: PlayedBy = X,
        val gameResult: GameResult? = null,
        val gameCounter: GameCounter = GameCounter()
    ) {
        enum class GameStatus { READY, ONGOING, FINISHED }
        enum class GameResult { XWINS, OWINS, DRAW }
    }

    sealed class Wish {
        object ResetGame : Wish()
        data class MakeMove(val index: Int) : Wish()
    }

    sealed class Action {
        data class Execute(val wish: Wish) : Action()
        object CheckBoard : Action()
        object ChangePlayer : Action()
    }

    sealed class Effect {
        object ResetGameEffect : Effect()
        data class MoveEffect(val index: Int, val player: PlayedBy) : Effect()
        data class ResultEffect(val result: State.GameResult) : Effect()
        data class ChangePlayerEffect(val player: PlayedBy) : Effect()
    }

    sealed class News {
        data class ResultNews(val result: State.GameResult) : News()
    }

    private class ActorImpl() : Actor<State, Action, Effect> {

        override fun invoke(state: State, action: Action): Observable<out Effect> {
            return when (action) {
                is Action.Execute -> when (action.wish) {
                    is ResetGame -> Observable.just(ResetGameEffect)
                    is MakeMove -> { makeMove(state, action.wish) }
                }
                is Action.CheckBoard ->
                    checkBoard(state)?.let {
                        Observable.merge(
                            Observable.just(ResultEffect(it)),
                            Observable.just(ChangePlayerEffect(state.currentPlayer))
                        )
                    }
                        ?: Observable.just(ChangePlayerEffect(state.currentPlayer))

                is Action.ChangePlayer -> Observable.just(ChangePlayerEffect(state.currentPlayer))
            }
        }

        private fun makeMove(state: State, action: MakeMove): Observable<out Effect> {
            return if (state.gameStatus != FINISHED && state.boardAsList[action.index] == EMPTY
            ) {
                Observable.just(MoveEffect(index = action.index, player = state.currentPlayer))
            } else {
                Observable.empty()
            }
        }

        private fun checkBoard(state: State): State.GameResult? {
            /**
             *   |0|1|2|
             *   |3|4|5|
             *   |6|7|8|
             */

            val resultWin = mutableListOf<List<PlayedBy?>>().apply {
                add(listOf(state.boardAsList[0], state.boardAsList[1], state.boardAsList[2]))
                add(listOf(state.boardAsList[3], state.boardAsList[4], state.boardAsList[5]))
                add(listOf(state.boardAsList[6], state.boardAsList[7], state.boardAsList[8]))
                add(listOf(state.boardAsList[0], state.boardAsList[3], state.boardAsList[6]))
                add(listOf(state.boardAsList[1], state.boardAsList[4], state.boardAsList[7]))
                add(listOf(state.boardAsList[2], state.boardAsList[5], state.boardAsList[8]))
                add(listOf(state.boardAsList[0], state.boardAsList[4], state.boardAsList[8]))
                add(listOf(state.boardAsList[2], state.boardAsList[4], state.boardAsList[6]))
            }
                .firstOrNull { combination -> combination.all { it == X } || combination.all { it == O } }
                ?.first()

            val resultDraw =
                resultWin == null && state.boardAsList.none { it == EMPTY }

            return when {
                resultWin == X -> State.GameResult.XWINS
                resultWin == O -> State.GameResult.OWINS
                resultDraw -> State.GameResult.DRAW
                else -> null
            }
        }
    }

    private class PostProcessorImpl : PostProcessor<Action, Effect, State> {
        override fun invoke(action: Action, effect: Effect, state: State): Action? {
            return when (effect) {
                is MoveEffect -> Action.CheckBoard
                else -> null
            }
        }
    }

    private class ReducerImpl : Reducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State {
            return when (effect) {

                is MoveEffect -> {

                    val mutable = state.boardAsList.toMutableList()
                    mutable[effect.index] = effect.player
                    val updatedCellList = mutable.toList()

                    state.copy(
                        boardAsList = updatedCellList,
                        gameStatus = ONGOING,
                    )
                }

                is ChangePlayerEffect -> state.copy(
                    currentPlayer = if (state.currentPlayer == X) O else X
                )

                is ResultEffect -> state.copy(
                    gameStatus = FINISHED,
                    gameResult = effect.result,
                    gameCounter = when (effect.result) {
                        State.GameResult.XWINS -> state.gameCounter.copy(
                            xWins = state.gameCounter.xWins + 1
                        )
                        State.GameResult.OWINS -> state.gameCounter.copy(
                            oWins = state.gameCounter.oWins + 1
                        )
                        State.GameResult.DRAW -> state.gameCounter.copy(
                            draws = state.gameCounter.draws + 1)
                    }
                )

                is ResetGameEffect -> State().copy(
                    gameCounter = state.gameCounter
                )
            }
        }
    }


    private class NewsPublisherImpl : NewsPublisher<Action, Effect, State, News> {
        override fun invoke(action: Action, effect: Effect, state: State): News? = when (effect) {
            is ResultEffect -> News.ResultNews(effect.result)
            else -> null
        }
    }


}

