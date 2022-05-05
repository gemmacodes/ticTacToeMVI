package com.sps.tictactoe.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.unit.dp
import com.sps.tictactoe.ui.theme.Champagne
import com.sps.tictactoe.ui.theme.Onyx
import com.sps.tictactoe.ui.theme.SuperLightGrey

//@Composable
//fun ResetButton(onResetClicked: () -> Unit) {
//    Button(
//        modifier = Modifier
//            .height(80.dp)
//            .padding(top = 15.dp, end = 15.dp)
//            .clip(CircleShape)
//            .border(
//                width = 2.dp,
//                color = Champagne,
//                shape = CircleShape
//            ),
//        onClick = onResetClicked
//    ) {
//        Icon(
//            Icons.Rounded.Refresh,
//            contentDescription = "refresh",
//            Modifier.size(30.dp)
//        )
//    }
//}

@Composable
fun ResetButton(onResetClicked: () -> Unit) {
    FloatingActionButton(
        modifier= Modifier.padding(top = 15.dp, end = 15.dp),
        onClick = onResetClicked,
        backgroundColor = SuperLightGrey,
        content = {
            Icon(
                Icons.Rounded.Refresh,
                contentDescription = "refresh",
                Modifier.size(30.dp)
            )
        }
    )
}


