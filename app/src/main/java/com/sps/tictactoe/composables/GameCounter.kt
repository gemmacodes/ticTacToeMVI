package com.sps.tictactoe.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sps.tictactoe.MainActivity
import com.sps.tictactoe.TicTacToeVM
import com.sps.tictactoe.ui.theme.Champagne
import com.sps.tictactoe.ui.theme.Mauve
import com.sps.tictactoe.ui.theme.Onyx
import com.sps.tictactoe.ui.theme.Pine

@Composable
fun GameCounter(
    gameCounter: TicTacToeVM.GameCounter,
    drawColor: Color = Onyx,
    xColor: Color = Mauve,
    oColor: Color = Pine
) {
    Surface(
        color = Champagne,
        shape = RoundedCornerShape(5.dp)
    ) {
    Row(
        modifier = Modifier.padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
            SimpleCounter("Draw", gameCounter.draws, drawColor)
            SimpleCounter("✳ Wins", gameCounter.xWins, xColor)
            SimpleCounter("⭗ Wins", gameCounter.oWins, oColor)
    }
    }
}

@Composable
fun SimpleCounter(title: String, count: Int, countColor: Color) {
    Column(modifier = Modifier.padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp),
            text = title,
            fontWeight = FontWeight.Bold,
            color = Onyx,
            fontSize = 20.sp
        )
        Text(
            modifier = Modifier.padding(horizontal = 10.dp),
            text = "$count",
            color = countColor,
            fontSize = 20.sp
        )
    }
}
