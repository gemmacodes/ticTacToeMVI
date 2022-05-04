package com.sps.tictactoe

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.PostProcessor
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.BaseFeature
import com.sps.tictactoe.BoardFeature.*
import com.sps.tictactoe.BoardFeature.Effect.*
import com.sps.tictactoe.BoardFeature.State.*
import com.sps.tictactoe.BoardFeature.State.GameStatus.*
import com.sps.tictactoe.BoardFeature.Wish.*
import com.sps.tictactoe.TicTacToeVM.GameCounter
import com.sps.tictactoe.TicTacToeVM.PlayedBy
import com.sps.tictactoe.TicTacToeVM.PlayedBy.*
import io.reactivex.Observable

class BoardFeature : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(),
    reducer = ReducerImpl(),
    postProcessor = PostProcessorImpl(),
    wishToAction = Action::Execute,
    actor = ActorImpl(),
    bootstrapper = BootstrapperImpl(),
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
        data class HandleMachineMove(val index: Int) : Wish()
    }

    sealed class Action {
        data class Execute(val wish: Wish) : Action()
        object CheckBoard : Action()
        object ChangePlayer : Action()
    }


    sealed class Effect {
        object ResetGameEffect : Effect()
        data class MoveEffect(val index: Int, val player: PlayedBy) : Effect()
        data class ResultEffect(val result: GameResult) : Effect()
        data class ChangePlayerEffect(val updatedBoard : List<PlayedBy>) : Effect()
    }

    sealed class News {
        data class ResultNews(val result: GameResult) : News()
        data class BoardUpdated(val board: List<PlayedBy>) : News()
    }

    private class ActorImpl : Actor<State, Action, Effect> {

        override fun invoke(state: State, action: Action): Observable<out Effect> {
            return when (action) {

                is Action.Execute -> when (action.wish) {
                    is ResetGame -> Observable.just(ResetGameEffect)
                    is HandleMachineMove -> {
                        handleMachineMove(state, action.wish)
                    }
                }

                is Action.CheckBoard ->
                    checkBoard(state)?.let {
                        Observable.merge(
                            Observable.just(ResultEffect(it)),
                            Observable.just(ChangePlayerEffect(state.boardAsList)),
                        )
                    }
                        ?: Observable.just(ChangePlayerEffect(state.boardAsList))

                is Action.ChangePlayer -> Observable.just(ChangePlayerEffect(state.boardAsList))
            }
        }


        private fun handleMachineMove(state: State, action: HandleMachineMove): Observable<out Effect> {
            return if (state.gameStatus != FINISHED ) {
                Observable.just(MoveEffect(index = action.index, player = state.currentPlayer))
            } else {
                Observable.empty()
            }
        }

        private fun checkBoard(state: State): GameResult? {

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
                resultWin == X -> GameResult.XWINS
                resultWin == O -> GameResult.OWINS
                resultDraw -> GameResult.DRAW
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
                        GameResult.XWINS -> state.gameCounter.copy(
                            xWins = state.gameCounter.xWins + 1
                        )
                        GameResult.OWINS -> state.gameCounter.copy(
                            oWins = state.gameCounter.oWins + 1
                        )
                        GameResult.DRAW -> state.gameCounter.copy(
                            draws = state.gameCounter.draws + 1
                        )
                    }
                )

                is ResetGameEffect -> {
                    State().copy(gameCounter = state.gameCounter)
                }

            }
        }
    }


    private class NewsPublisherImpl : NewsPublisher<Action, Effect, State, News> {
        override fun invoke(action: Action, effect: Effect, state: State): News? = when (effect) {
            is ResultEffect -> News.ResultNews(effect.result)
            is ChangePlayerEffect -> News.BoardUpdated(effect.updatedBoard)
            else -> null
        }
    }

}

