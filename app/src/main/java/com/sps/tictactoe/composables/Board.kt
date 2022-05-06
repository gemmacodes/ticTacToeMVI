package com.sps.tictactoe.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.sps.tictactoe.TicTacToeVM
import com.sps.tictactoe.ui.theme.Mauve
import com.sps.tictactoe.ui.theme.Pine

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameBoard(board: TicTacToeVM.Board, onCellClicked: (Int) -> Unit) {
    val cells = board.asCellList()
    LazyVerticalGrid(
        modifier = Modifier.padding(horizontal=10.dp, vertical = 20.dp),
        cells = GridCells.Fixed(3),
        contentPadding = PaddingValues(vertical = 3.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    )
    {
        items(cells.count()) { index ->
            BoardCell(content = cells[index]) {
                onCellClicked(index)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun BoardCell(content: TicTacToeVM.PlayedBy, onCellClicked: () -> Unit) {
    var contentState by remember { mutableStateOf(TicTacToeVM.PlayedBy.EMPTY) }
    contentState = content

    Card(
        modifier = Modifier
            .border(
                BorderStroke(3.dp, MaterialTheme.colors.primary),
                shape = RoundedCornerShape(5.dp)
            )
            .aspectRatio(1f),
        onClick = onCellClicked,

        ) {

        AnimatedContent(
            targetState = contentState,
        ) { piece ->
            when (piece) {
                TicTacToeVM.PlayedBy.X -> PieceX()
                TicTacToeVM.PlayedBy.O -> PieceO()
                TicTacToeVM.PlayedBy.EMPTY -> {}
            }
        }
    }
}

@Composable
fun PieceO(color: Color = Pine) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        val animateFloat = remember { Animatable(0f) }
        LaunchedEffect(animateFloat) {
            animateFloat.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = LinearEasing))
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
//            val canvasWidth = size.width
//            val canvasHeight = size.height
            val radius = (size.minDimension / 2) * 0.70f
            val stroke = radius * 0.1f.dp.toPx()
            val sizeArc = size/1.75F

            drawArc(
                color = color,
                startAngle = 0f,
                sweepAngle = 360f * animateFloat.value,
                useCenter = false,
                topLeft = Offset((size.width - sizeArc.width)/2f,(size.height - sizeArc.height)/2f),
                size = sizeArc,
                style = Stroke(width = stroke)
            )

//            drawCircle(
//                color = color,
//                center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
//                radius = radius,
//                style = Stroke(width = stroke)
//            )
//            drawCircle(
//                color = color,
//                center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
//                radius = (size.minDimension / 2) * 0.35f,
//                style = Stroke(width = stroke)
//            )
        }
    }
}

@Composable
fun PieceX(color: Color = Mauve) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        val animateFloat = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            animateFloat.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 250, easing = LinearEasing))
        }


        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val rectH = canvasHeight * 0.75f
            val rectW = rectH * 0.15f

            drawLine(
                start = Offset(
                    x = (3 * canvasWidth) / 4F,
                    y =  canvasHeight / 4F
                ),
                end = Offset(
                    x = (canvasWidth/4F) * animateFloat.value,
                    y = ((3 * canvasHeight) / 4F) * animateFloat.value),
                color = color,
                strokeWidth = rectW
            )

            drawLine(
                start = Offset(
                    x= canvasWidth / 4F,
                    y = canvasHeight / 4F
                ),
                end = Offset(
                    x = ((3 * canvasWidth) / 4f) * animateFloat.value,
                    y = ((3 * canvasHeight) / 4f) * animateFloat.value
                ),
                color = color,
                strokeWidth = rectW
            )


//            drawRect(
//                color = color,
//                topLeft = Offset(
//                    x = (canvasWidth / 2F) - rectW / 2f,
//                    y = (canvasHeight - rectH) / 2f
//                ),
//                size = Size(height = rectH, width = rectW)
//            )
//            rotate(45f) {
//                drawRect(
//                    color = color,
//                    topLeft = Offset(
//                        x = (canvasWidth / 2F) - rectW / 2f,
//                        y = (canvasHeight - rectH) / 2f
//                    ),
//                    size = Size(height = rectH, width = rectW)
//                )
//            }
//            rotate(90f) {
//                drawRect(
//                    color = color,
//                    topLeft = Offset(
//                        x = (canvasWidth / 2F) - rectW / 2f,
//                        y = (canvasHeight - rectH) / 2f
//                    ),
//                    size = Size(height = rectH, width = rectW)
//                )
//            }
//            rotate(135f) {
//                drawRect(
//                    color = color,
//                    topLeft = Offset(
//                        x = (canvasWidth / 2F) - rectW / 2f,
//                        y = (canvasHeight - rectH) / 2f
//                    ),
//                    size = Size(height = rectH, width = rectW)
//                )
//            }
        }
    }
}
