package com.sps.tictactoe.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sps.tictactoe.MainActivity
import com.sps.tictactoe.TicTacToeVM

@Composable
fun GameCounter(
    gameCounter: TicTacToeVM.GameCounter,
    drawColor: Color = Color.Black,
    xColor: Color = Color.Red,
    oColor: Color = Color.Blue
) {
    Row(
        modifier = Modifier.padding(20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SimpleCounter("Draws", gameCounter.draws, drawColor)
        SimpleCounter("✳ Wins", gameCounter.xWins, xColor)
        SimpleCounter("⭗ Wins", gameCounter.oWins, oColor)
    }
}

@Composable
fun SimpleCounter(title: String, count: Int, countColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp),
            text = title,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
            fontSize = 30.sp
        )
        Text(
            modifier = Modifier.padding(horizontal = 10.dp),
            text = "$count",
            color = countColor,
            fontSize = 30.sp
        )
    }
}
