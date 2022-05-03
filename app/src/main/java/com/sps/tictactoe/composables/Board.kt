package com.sps.tictactoe.composables

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.sps.tictactoe.TicTacToeVM

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameBoard(board: TicTacToeVM.Board, onCellClicked: (Int) -> Unit) {
    val cells = board.asCellList()
    LazyVerticalGrid(
        modifier = Modifier.padding(10.dp),
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BoardCell(content: TicTacToeVM.PlayedBy, onCellClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .border(
                BorderStroke(3.dp, MaterialTheme.colors.primary),
                shape = RoundedCornerShape(5.dp)
            )
            .aspectRatio(1f),
        onClick = onCellClicked,

        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            when (content) {
                TicTacToeVM.PlayedBy.EMPTY -> { /* NoOp */
                }
                TicTacToeVM.PlayedBy.X -> PieceX()
                TicTacToeVM.PlayedBy.O -> PieceO()
            }
        }
    }
}

@Composable
fun PieceO(color: Color = Color.Blue) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = (size.minDimension / 2) * 0.70f
        val stroke = radius * 0.1f.dp.toPx()
        drawCircle(
            color = color,
            center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
            radius = radius,
            style = Stroke(width = stroke)
        )
        drawCircle(
            color = color,
            center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
            radius = (size.minDimension / 2) * 0.35f,
            style = Stroke(width = stroke)
        )
    }
}

@Composable
fun PieceX(color: Color = Color.Red) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val rectH = canvasHeight * 0.75f
        val rectW = rectH * 0.25f
        drawRect(
            color = color,
            topLeft = Offset(
                x = (canvasWidth / 2F) - rectW / 2f,
                y = (canvasHeight - rectH) / 2f
            ),
            size = Size(height = rectH, width = rectW)
        )
        rotate(45f) {
            drawRect(
                color = color,
                topLeft = Offset(
                    x = (canvasWidth / 2F) - rectW / 2f,
                    y = (canvasHeight - rectH) / 2f
                ),
                size = Size(height = rectH, width = rectW)
            )
        }
        rotate(90f) {
            drawRect(
                color = color,
                topLeft = Offset(
                    x = (canvasWidth / 2F) - rectW / 2f,
                    y = (canvasHeight - rectH) / 2f
                ),
                size = Size(height = rectH, width = rectW)
            )
        }
        rotate(135f) {
            drawRect(
                color = color,
                topLeft = Offset(
                    x = (canvasWidth / 2F) - rectW / 2f,
                    y = (canvasHeight - rectH) / 2f
                ),
                size = Size(height = rectH, width = rectW)
            )
        }
    }
}
