package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.DailyChartPoint
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun IslamicGeometricBackground(
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.09f)
) {
    Canvas(modifier = modifier) {
        val spacing = size.minDimension / 4.2f
        if (spacing <= 0f) return@Canvas
        val cols = (size.width / spacing).toInt() + 2
        val rows = (size.height / spacing).toInt() + 2
        val radius = spacing * 0.34f
        for (r in 0..rows) {
            for (c in 0..cols) {
                val cx = c * spacing + if (r % 2 == 1) spacing * 0.5f else 0f
                val cy = r * spacing
                drawEightPointStar(
                    center = Offset(cx, cy),
                    outerRadius = radius,
                    innerRadius = radius * 0.56f,
                    color = lineColor
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawEightPointStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    color: Color
) {
    val path = Path()
    val points = 16
    for (i in 0 until points) {
        val angle = (i * 2.0 * PI / points) - PI / 2.0
        val r = if (i % 2 == 0) outerRadius else innerRadius
        val x = center.x + (r * cos(angle)).toFloat()
        val y = center.y + (r * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path = path, color = color, style = Stroke(width = 1.6f))
}

@Composable
fun TasbihProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    goldColor: Color = MaterialTheme.colorScheme.secondary
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 14.dp.toPx()
        val diameter = min(size.width, size.height) - strokeWidth
        val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
        val arcSize = Size(diameter, diameter)

        // Subtle outer ornamental ring
        drawCircle(
            color = goldColor.copy(alpha = 0.16f),
            radius = diameter / 2f + 12.dp.toPx(),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Background track
        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Active emerald-to-gold gradient arc
        val clamped = progress.coerceIn(0f, 1f)
        if (clamped > 0f) {
            drawArc(
                brush = Brush.sweepGradient(listOf(primaryColor, goldColor, primaryColor)),
                startAngle = -90f,
                sweepAngle = 360f * clamped,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun ActivityBarChart(
    points: List<DailyChartPoint>,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val gold = MaterialTheme.colorScheme.secondary
    val track = MaterialTheme.colorScheme.surfaceVariant
    val maxVal = (points.maxOfOrNull { it.athkarCount * 5 + it.tasbihCount } ?: 10).coerceAtLeast(10)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 4.dp)
        ) {
            if (points.isEmpty()) return@Canvas
            val slotWidth = size.width / points.size
            val barWidth = (slotWidth * 0.48f).coerceIn(8.dp.toPx(), 24.dp.toPx())
            val maxH = size.height - 8.dp.toPx()

            points.forEachIndexed { idx, pt ->
                val score = pt.athkarCount * 5 + pt.tasbihCount
                val ratio = (score.toFloat() / maxVal.toFloat()).coerceIn(0f, 1f)
                val barH = (maxH * ratio).coerceAtLeast(if (score > 0) 10.dp.toPx() else 4.dp.toPx())
                val left = idx * slotWidth + (slotWidth - barWidth) / 2f

                // Track background
                drawRoundRect(
                    color = track.copy(alpha = 0.55f),
                    topLeft = Offset(left, 4.dp.toPx()),
                    size = Size(barWidth, maxH),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )

                // Filled bar
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(gold, primary)),
                    topLeft = Offset(left, size.height - barH),
                    size = Size(barWidth, barH),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val step = if (points.size > 8) 2 else 1
            points.forEachIndexed { idx, pt ->
                if (idx % step == 0) {
                    Text(
                        text = pt.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
