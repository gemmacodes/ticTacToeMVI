package com.sps.tictactoe.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResetButton(onResetClicked: () -> Unit) {
    Button(
        modifier = Modifier
            .height(50.dp)
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        onClick = onResetClicked
    ) {
        Text(
            text = "Start Again",
            fontSize = 20.sp
        )
    }
}
