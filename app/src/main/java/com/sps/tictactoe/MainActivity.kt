package com.sps.tictactoe

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sps.tictactoe.BoardFeature.State.*
import com.sps.tictactoe.BoardFeature.State.GameResult.*
import com.sps.tictactoe.HumanUiEvent.*
import com.sps.tictactoe.composables.GameBoard
import com.sps.tictactoe.composables.GameCounter
import com.sps.tictactoe.composables.ResetButton
import com.sps.tictactoe.ui.theme.Mauve
import com.sps.tictactoe.ui.theme.Pine
import com.sps.tictactoe.ui.theme.TicTacToeTheme
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject


class MainActivity : ComponentActivity(), ObservableSource<HumanUiEvent>,
    Consumer<TicTacToeVM> {
    private val startSignal = PublishSubject.create<Unit>()
    private val boardState = BoardFeature(startSignal.firstOrError())
    private val machineState = MachineFeature()
    private val bindings = TicTacToeBindings(
        this, boardState, machineState
    )
    private var viewModel: TicTacToeVM? by mutableStateOf(null)
    private val subject: PublishSubject<HumanUiEvent> = PublishSubject.create()

    fun showResult(gameResult: GameResult) {
        when (gameResult) {
            XWINS -> Toast.makeText(
                this,
                "Game over, X wins!",
                Toast.LENGTH_SHORT
            )
                .show()
            OWINS -> Toast.makeText(
                this,
                "Game over, O wins!",
                Toast.LENGTH_SHORT
            )
                .show()
            DRAW -> Toast.makeText(
                this,
                "Game over - It's a draw!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindings()
        setContent {
            TicTacToeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.End
                    ){
                        ResetButton(::onResetClicked)
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(modifier = Modifier.padding(bottom = 20.dp)) {
                            Text(
                                text = "Tic",
                                fontWeight = FontWeight.Bold,
                                color = Mauve,
                                fontSize = 40.sp
                            )
                            Text(
                                text = "Tac",
                                fontWeight = FontWeight.Bold,
                                color = Pine,
                                fontSize = 40.sp
                            )
                            Text(
                                text = "Toe",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colors.primary,
                                fontSize = 40.sp
                            )
                        }
                        viewModel?.let{ vm ->
                            GameCounter(gameCounter = vm.gameCounter)
                            GameBoard(board = vm.board) {
                                onCellClicked()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startSignal.onNext(Unit)
    }

    private fun setBindings() {
        bindings.setup(this)
    }

    override fun subscribe(observer: Observer<in HumanUiEvent>) {
        subject.subscribe(observer)
    }

    override fun accept(viewModel: TicTacToeVM) {
        this.viewModel = viewModel
    }

    private fun onResetClicked() {
        subject.onNext(ResetClicked)
    }

    private fun onCellClicked() {

    }

}






