package com.sps.tictactoe

import androidx.lifecycle.LifecycleOwner
import com.badoo.binder.using
import com.badoo.mvicore.android.AndroidBindings
import com.sps.tictactoe.BoardFeature.*
import com.sps.tictactoe.HumanFeature.News.HumanMoveFinished
import com.sps.tictactoe.MachineFeature.News.MachineMoveFinished
import io.reactivex.functions.Consumer

internal class TicTacToeBindings(
    lifecycleOwner: LifecycleOwner,
    private val boardFeature: BoardFeature,
    private val humanFeature: HumanFeature,
    private val machineFeature: MachineFeature
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


    object UiEventBoardTransformer : (HumanUiEvent) -> Wish? {
        override fun invoke(event: HumanUiEvent): Wish =
            when (event) {
                is HumanUiEvent.CellClicked -> Wish.HandleHumanMove(event.index)
                is HumanUiEvent.ResetClicked -> Wish.ResetGame
            }
    }

    object MachineToBoard : (MachineFeature.News) -> Wish? {
        override fun invoke(news: MachineFeature.News): Wish =
            when (news) {
                is MachineMoveFinished ->
                    Wish.HandleMachineMove(index = news.index)
            }
    }

    object HumanToBoard : (HumanFeature.News) -> Wish? {
        override fun invoke(news: HumanFeature.News): Wish =
            when (news) {
                is HumanMoveFinished ->
                    Wish.HandleHumanMove(index = news.index)
            }
    }

    object BoardToMachine : (News) -> MachineFeature.Wish? {
        override fun invoke(news: News): MachineFeature.Wish? =
            when (news) {
                is News.MoveFinished ->
                    if (news.player == TicTacToeVM.PlayedBy.O)
                        MachineFeature.Wish.StartMachineMove(
                            news.board
                        ) else null
                else -> null
            }
    }


    override fun setup(view: MainActivity) {
        binder.bind(boardFeature to view using ViewModelTransformer)
        binder.bind(view to boardFeature using UiEventBoardTransformer)
        binder.bind(boardFeature.news to Consumer {
            when (it) {
                is News.ResultNews -> view.showResult(it.result)
                else -> {}
            }
        })
        binder.bind(humanFeature.news to boardFeature using HumanToBoard)
        binder.bind(machineFeature.news to boardFeature using MachineToBoard)
        binder.bind(boardFeature.news to machineFeature using BoardToMachine)
    }
}


sealed class HumanUiEvent {
    data class CellClicked(val board: List<TicTacToeVM.PlayedBy>, val index: Int) : HumanUiEvent()
    object ResetClicked : HumanUiEvent()
}


