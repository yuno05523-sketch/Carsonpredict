package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HistoryItem
import com.example.data.model.PredictionResultStatus
import com.example.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun HistoryScreen(
    historyList: List<HistoryItem>,
    currentFilter: String,
    onFilterChanged: (String) -> Unit
) {
    val filters = listOf("Tous", "Validés", "Perdus", "En cours")

    val filteredList = when (currentFilter) {
        "Validés" -> historyList.filter { it.status == PredictionResultStatus.GAGNE }
        "Perdus" -> historyList.filter { it.status == PredictionResultStatus.PERDU }
        "En cours" -> historyList.filter { it.status == PredictionResultStatus.EN_ATTENTE }
        else -> historyList
    }

    // Calculs de performance réels
    val finishedItems = historyList.filter { it.status == PredictionResultStatus.GAGNE || it.status == PredictionResultStatus.PERDU }
    val wonCount = finishedItems.count { it.status == PredictionResultStatus.GAGNE }
    val totalFinished = finishedItems.size
    val winRate = if (totalFinished > 0) (wonCount.toDouble() / totalFinished * 100).roundToInt() else 0

    val totalRoi = if (totalFinished > 0) {
        (finishedItems.sumOf { it.netRoiPercentage } / totalFinished).roundToInt()
    } else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("history_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // KPI Summary Cards
        item {
            PerformanceSummaryCard(
                winRate = winRate,
                wonCount = wonCount,
                totalFinished = totalFinished,
                averageRoi = totalRoi
            )
        }

        // Filter chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    val isSelected = filter == currentFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterChanged(filter) },
                        label = {
                            Text(
                                text = filter,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else TextPrimary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldNeon,
                            containerColor = PitchSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) EmeraldNeon else PitchBorder
                        ),
                        modifier = Modifier.testTag("history_filter_$filter")
                    )
                }
            }
        }

        // Header list
        item {
            Text(
                text = "Historique Vérifié (${filteredList.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // List
        items(filteredList, key = { it.id }) { item ->
            HistoryCard(item = item)
        }
    }
}

@Composable
fun PerformanceSummaryCard(
    winRate: Int,
    wonCount: Int,
    totalFinished: Int,
    averageRoi: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("kpi_summary_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PitchSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Statistiques de Performance Réelle",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(EmeraldNeon.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = "Audit IA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldNeon)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                KpiItem(
                    label = "Taux de Réussite",
                    value = "$winRate%",
                    valueColor = EmeraldNeon,
                    subtitle = "$wonCount / $totalFinished validés"
                )
                KpiItem(
                    label = "ROI Moyen / Coupon",
                    value = if (averageRoi >= 0) "+$averageRoi%" else "$averageRoi%",
                    valueColor = if (averageRoi >= 0) GoldTrophy else DangerRed,
                    subtitle = "Rendement net"
                )
                KpiItem(
                    label = "Série en Cours",
                    value = "2 V",
                    valueColor = AccentCyan,
                    subtitle = "Série consécutive"
                )
            }
        }
    }
}

@Composable
fun KpiItem(
    label: String,
    value: String,
    valueColor: Color,
    subtitle: String
) {
    Column {
        Text(text = label, fontSize = 11.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = subtitle, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
fun HistoryCard(item: HistoryItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_card_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PitchSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.date,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${item.type}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }

                StatusBadge(status = item.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.selectionsSummary,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cote totale : @${String.format("%.2f", item.totalOdds)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldTrophy
                )

                if (item.status == PredictionResultStatus.GAGNE) {
                    Text(
                        text = "Profit : +${item.netRoiPercentage.roundToInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldNeon
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: PredictionResultStatus) {
    val (bgColor, textColor, text) = when (status) {
        PredictionResultStatus.GAGNE -> Triple(EmeraldNeon.copy(alpha = 0.2f), EmeraldNeon, "VALIDÉ ✓")
        PredictionResultStatus.PERDU -> Triple(DangerRed.copy(alpha = 0.2f), DangerRed, "PERDU ✗")
        PredictionResultStatus.EN_ATTENTE -> Triple(WarningOrange.copy(alpha = 0.2f), WarningOrange, "EN COURS ⏳")
        PredictionResultStatus.REPORTE -> Triple(TextMuted.copy(alpha = 0.2f), TextMuted, "REPORTÉ ⚠️")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
    }
}
