package com.sps.tictactoe.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ResetButton(onResetClicked: () -> Unit) {
    Button(
        modifier = Modifier
            .height(75.dp)
            .padding(top = 10.dp, end = 10.dp)
            .clip(CircleShape)
            .border(
                width = 2.dp,
                color = Color.Red,
                shape = CircleShape
            ),
        onClick = onResetClicked
    ) {
        Icon(
            Icons.Rounded.Refresh,
            contentDescription = "refresh",
            Modifier.size(30.dp)
        )
    }
}
