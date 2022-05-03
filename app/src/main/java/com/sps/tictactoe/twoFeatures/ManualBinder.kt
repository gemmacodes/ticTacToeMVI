package com.sps.tictactoe.twoFeatures

import androidx.lifecycle.LifecycleOwner
import com.badoo.binder.using
import com.badoo.mvicore.android.AndroidBindings
import com.sps.tictactoe.MainActivity
import com.sps.tictactoe.TicTacToeVM
import io.reactivex.functions.Consumer

//class ManualBinder {
//
//    private val humanFeature = HumanFeature()
//    private val dummyFeature = DummyFeature()
//
//    private val binder = Binder()
//
//    object HumanToDummy : (HumanFeature.State) -> DummyFeature.Wish? {
//        override fun invoke(state: HumanFeature.State): DummyFeature.Wish? =
//            if (!state.humanPlayer) {
//                DummyFeature.Wish.StartMachineMove(board = state.boardAsList)
//            } else null
//    }
//
//    object DummyToHuman : (DummyFeature.State) -> HumanFeature.Wish? {
//        override fun invoke(state: DummyFeature.State): HumanFeature.Wish? =
//            if (state.index != -1) {
//                HumanFeature.Wish.StartMachineMove(index = state.index)
//            } else null
//    }
//
//    fun machineHumanConnection() {
//        with(binder) {
//            bind(humanFeature to dummyFeature using HumanToDummy)
//            bind(dummyFeature to humanFeature using DummyToHuman)
//        }
//    }
//
//}

internal class ManualBinder(
    lifecycleOwner: LifecycleOwner,
    private val humanFeature: HumanFeature,
    private val dummyFeature: DummyFeature,
    private val featureStateToViewModel: ViewModelTransformer,
    private val uiEventToWish: UiEventTransformer,
) : AndroidBindings<MainActivity>(lifecycleOwner) {


    object ViewModelTransformer : (HumanFeature.State) -> TicTacToeVM {
        override fun invoke(state: HumanFeature.State): TicTacToeVM =
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

    object UiEventTransformer : (HumanUiEvent) -> HumanFeature.Wish? {
        override fun invoke(event: HumanUiEvent): HumanFeature.Wish? =
            when (event) {
                is HumanUiEvent.CellClicked -> HumanFeature.Wish.HumanMove(event.index)
                is HumanUiEvent.ResetClicked -> HumanFeature.Wish.ResetGame
                else -> null
            }
    }


    object DummyToHuman : (DummyFeature.State) -> HumanFeature.Wish? {
        override fun invoke(state: DummyFeature.State): HumanFeature.Wish? =
            if (state.index != -1) {
                HumanFeature.Wish.EndMachineMove(index = state.index)
            } else null
    }


    object HumanToDummy : (HumanFeature.State) -> DummyFeature.Wish? {
        override fun invoke(state: HumanFeature.State): DummyFeature.Wish? =
            if (!state.humanPlayer) {
                DummyFeature.Wish.StartMachineMove(board = state.boardAsList)
            } else null
    }



    override fun setup(view: MainActivity) {
        binder.bind(humanFeature to view using featureStateToViewModel)
        binder.bind(view to humanFeature using uiEventToWish)
        binder.bind(humanFeature.news to Consumer {
            when (it) {
                is HumanFeature.News.ResultNews -> view.showResult(it.result)
            }
        })
        binder.bind(humanFeature to dummyFeature using HumanToDummy)
        binder.bind(dummyFeature to humanFeature using DummyToHuman)

    }


}

sealed class HumanUiEvent {
    data class CellClicked(val index: Int) : HumanUiEvent()
    object ResetClicked : HumanUiEvent()
}


