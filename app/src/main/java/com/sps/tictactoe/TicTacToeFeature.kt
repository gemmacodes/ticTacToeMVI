package com.sps.tictactoe

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.PostProcessor
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.BaseFeature
import com.sps.tictactoe.TicTacToeFeature.*
import com.sps.tictactoe.TicTacToeFeature.Effect.*
import com.sps.tictactoe.TicTacToeFeature.State.GameStatus
import com.sps.tictactoe.TicTacToeFeature.Wish.MakeMove
import com.sps.tictactoe.TicTacToeFeature.Wish.ResetGame
import com.sps.tictactoe.TicTacToeVM.GameCounter
import com.sps.tictactoe.TicTacToeVM.PlayingState
import com.sps.tictactoe.TicTacToeVM.PlayingState.*
import io.reactivex.Observable

/**
 * Complete State and feature with required logic..
 * Right now this is extending ActorReducerFeature. Feel Free To change it if you need it.
 * This is just an example, so if you don't need something, feel free to delete it.
 * E.g: You don't need the News? Delete them and pass <Nothing> instead as News Type
 * ActorReducerFeature<Wish, Effect, State, Nothing>
 */

class TicTacToeFeature : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(),
    reducer = ReducerImpl(),
    postProcessor = PostProcessorImpl(),
    wishToAction = Action::Execute,
    actor = ActorImpl(),
    newsPublisher = NewsPublisherImpl()
) {

    data class State(
        val cellList: List<PlayingState> = listOf(
            EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY
        ),
        val gameStatus: GameStatus = GameStatus.READY,
        val currentPlayer: PlayingState = X,
        val gameResult: GameResult? = null,
        val gameCounter: GameCounter = GameCounter()
    ) {
        enum class GameStatus { READY, ONGOING, FINISHED }
        enum class GameResult { XWINS, OWINS, DRAW }
    }

    sealed class Effect {
        object ResetGameEffect : Effect()
        data class MoveEffect(val index: Int, val player: PlayingState) : Effect()
        data class ResultEffect(val result: State.GameResult) : Effect()
    }

    sealed class Wish {
        object ResetGame : Wish()
        data class MakeMove(val index: Int) : Wish()
    }

    sealed class Action {
        data class Execute(val wish: Wish) : Action()
        object CheckBoard : Action()
    }

    sealed class News {
        data class ResultNews(val result: State.GameResult) : News()
    }

    private class ActorImpl : Actor<State, Action, Effect> {

        override fun invoke(state: State, action: Action): Observable<out Effect> {
            return when (action) {
                is Action.Execute -> when (action.wish) {
                    is ResetGame -> Observable.just(ResetGameEffect)
                    is MakeMove -> {
                        makeMove(state, action.wish)
                    }
                }
                is Action.CheckBoard -> checkBoard(state)?.let { Observable.just(ResultEffect(it)) }
                    ?: Observable.empty()
            }
        }

        private fun makeMove(state: State, action: MakeMove): Observable<out Effect> {
            return if (state.gameStatus != GameStatus.FINISHED
                && state.cellList[action.index] == EMPTY
            ) {
                Observable.just(
                    MoveEffect(index = action.index, player = state.currentPlayer),
                )
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

            val resultWin = mutableListOf<List<PlayingState?>>().apply {
                add(listOf(state.cellList[0], state.cellList[1], state.cellList[2]))
                add(listOf(state.cellList[3], state.cellList[4], state.cellList[5]))
                add(listOf(state.cellList[6], state.cellList[7], state.cellList[8]))
                add(listOf(state.cellList[0], state.cellList[3], state.cellList[6]))
                add(listOf(state.cellList[1], state.cellList[4], state.cellList[7]))
                add(listOf(state.cellList[2], state.cellList[5], state.cellList[8]))
                add(listOf(state.cellList[0], state.cellList[4], state.cellList[8]))
                add(listOf(state.cellList[2], state.cellList[4], state.cellList[6]))
            }
                .any { possibility -> possibility.all { if (state.currentPlayer == O) it == X else it == O } }

            val resultDraw =
                !resultWin && state.cellList.all { it == O || it == X }

            return when {
                resultWin && state.currentPlayer == O -> State.GameResult.XWINS
                resultWin && state.currentPlayer == X -> State.GameResult.OWINS
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

                    val mutable = state.cellList.toMutableList()
                    mutable[effect.index] = effect.player
                    val updatedCellList = mutable.toList()

                    state.copy(
                        cellList = updatedCellList,
                        gameStatus = GameStatus.ONGOING,
                        currentPlayer = if (state.currentPlayer == X) O else X
                    )
                }

                is ResultEffect -> state.copy(
                    gameStatus = GameStatus.FINISHED,
                    gameResult = effect.result,
                    gameCounter = when (effect.result) {
                        State.GameResult.XWINS -> GameCounter(
                            xWins = state.gameCounter.xWins + 1,
                            oWins = state.gameCounter.oWins,
                            draws = state.gameCounter.draws
                        )
                        State.GameResult.OWINS -> GameCounter(
                            xWins = state.gameCounter.xWins,
                            oWins = state.gameCounter.oWins + 1,
                            draws = state.gameCounter.draws
                        )
                        State.GameResult.DRAW -> GameCounter(
                            xWins = state.gameCounter.xWins,
                            oWins = state.gameCounter.oWins,
                            draws = state.gameCounter.draws + 1
                        )
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

