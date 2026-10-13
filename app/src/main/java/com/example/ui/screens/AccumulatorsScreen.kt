package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Accumulator
import com.example.data.model.AccumulatorType
import com.example.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun AccumulatorsScreen(
    accumulators: List<Accumulator>,
    selectedType: AccumulatorType,
    onSelectType: (AccumulatorType) -> Unit,
    onCouponCopied: (String) -> Unit
) {
    val context = LocalContext.current
    val currentAcc = accumulators.find { it.type == selectedType }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("accumulators_screen")
    ) {
        // Tab selector for Cote 3, Cote 4, Cote 5
        TabRow(
            selectedTabIndex = selectedType.ordinal,
            containerColor = PitchSurface,
            contentColor = EmeraldNeon,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedType.ordinal]),
                    color = when (selectedType) {
                        AccumulatorType.COTE_3 -> EmeraldNeon
                        AccumulatorType.COTE_4 -> GoldTrophy
                        AccumulatorType.COTE_5 -> DangerRed
                    }
                )
            }
        ) {
            AccumulatorType.values().forEach { type ->
                val isSelected = type == selectedType
                Tab(
                    selected = isSelected,
                    onClick = { onSelectType(type) },
                    text = {
                        Text(
                            text = when (type) {
                                AccumulatorType.COTE_3 -> "Cote ~3\nSécurité"
                                AccumulatorType.COTE_4 -> "Cote ~4\nÉquilibré"
                                AccumulatorType.COTE_5 -> "Cote ~5\nAudacieux"
                            },
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) TextPrimary else TextMuted
                        )
                    },
                    modifier = Modifier.testTag("tab_${type.name}")
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quality & criteria banner
            item {
                AccumulatorHeaderCard(type = selectedType)
            }

            if (currentAcc == null || !currentAcc.isReliable || currentAcc.selections.isEmpty()) {
                // Rule: "Si aucun combiné ne satisfait les critères de qualité, afficher 'Aucun combiné suffisamment fiable aujourd’hui'"
                item {
                    UnreliableCombinerCard()
                }
            } else {
                // Total calculated odds & probability card
                item {
                    TotalOddsCard(
                        accumulator = currentAcc,
                        onCopyCoupon = {
                            val textSlip = buildString {
                                appendLine("🎟️ COUPON CARSONPREDICT AI - ${currentAcc.title}")
                                appendLine("📅 Date : ${currentAcc.date}")
                                appendLine("📈 Cote totale calculée : @${String.format("%.2f", currentAcc.totalOdds)}")
                                appendLine("🎯 Probabilité estimée : ${(currentAcc.estimatedCombinedProbability * 100).roundToInt()}%")
                                appendLine("--- SÉLECTIONS ---")
                                currentAcc.selections.forEach { sel ->
                                    appendLine("• ${sel.matchTitle} (${sel.league})")
                                    appendLine("  Marché : ${sel.market} @${String.format("%.2f", sel.odds)} (P: ${(sel.estimatedProbability * 100).roundToInt()}%)")
                                }
                                appendLine("⚠️ Jeu responsable. Aucun prono n'est garanti à 100%.")
                            }

                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("CarsonPredict Coupon", textSlip)
                            clipboard.setPrimaryClip(clip)
                            onCouponCopied("Coupon copié dans le presse-papier !")
                        }
                    )
                }

                // AI Overall Rationale
                item {
                    RationaleCard(rationale = currentAcc.overallRationale)
                }

                // List of retained matches
                item {
                    Text(
                        text = "Matchs Retenus dans la Sélection (${currentAcc.selections.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                items(currentAcc.selections.size) { index ->
                    val sel = currentAcc.selections[index]
                    SelectionDetailCard(selection = sel, index = index + 1)
                }
            }

            // Responsible betting disclaimer
            item {
                ResponsibleBettingNotice()
            }
        }
    }
}

@Composable
fun AccumulatorHeaderCard(type: AccumulatorType) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PitchSurfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when (type) {
                            AccumulatorType.COTE_3 -> EmeraldNeon.copy(alpha = 0.2f)
                            AccumulatorType.COTE_4 -> GoldTrophy.copy(alpha = 0.2f)
                            AccumulatorType.COTE_5 -> DangerRed.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (type) {
                        AccumulatorType.COTE_3 -> Icons.Default.Shield
                        AccumulatorType.COTE_4 -> Icons.Default.AutoAwesome
                        AccumulatorType.COTE_5 -> Icons.Default.Warning
                    },
                    contentDescription = null,
                    tint = when (type) {
                        AccumulatorType.COTE_3 -> EmeraldNeon
                        AccumulatorType.COTE_4 -> GoldTrophy
                        AccumulatorType.COTE_5 -> DangerRed
                    },
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = type.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Objectif cote : ${type.targetOddsRange} • ${type.maxRisk}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun TotalOddsCard(
    accumulator: Accumulator,
    onCopyCoupon: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("total_odds_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PitchSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldNeon))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "COTE TOTALE CALCULÉE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldTrophy
                    )
                    Text(
                        text = "@${String.format("%.2f", accumulator.totalOdds)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldNeon
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "PROBABILITÉ ESTIMÉE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                    Text(
                        text = "${(accumulator.estimatedCombinedProbability * 100).roundToInt()}%",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onCopyCoupon,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("copy_coupon_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon, contentColor = Color.Black)
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Copier le coupon de paris",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun RationaleCard(rationale: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PitchSurfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = GoldTrophy,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Stratégie de l'algorithme :",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldTrophy
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = rationale,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
fun SelectionDetailCard(
    selection: com.example.data.model.AccumulatorSelection,
    index: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(PitchSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "$index", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldNeon)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selection.league,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoldTrophy)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "@${String.format("%.2f", selection.odds)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = selection.matchTitle,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Marché : ",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Text(
                    text = selection.market,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldNeon
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "• ${(selection.estimatedProbability * 100).roundToInt()}% prob.",
                    fontSize = 12.sp,
                    color = AccentCyan
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(PitchSurfaceVariant)
                    .padding(8.dp)
            ) {
                Text(
                    text = "💡 Motif : ${selection.reason}",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun UnreliableCombinerCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("unreliable_combiner_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PitchSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(WarningOrange))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = WarningOrange,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Aucun combiné suffisamment fiable aujourd'hui",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Conformément à la règle d'or du robot CarsonPredict AI, aucun match n'est ajouté artificiellement pour forcer une cote. Les critères de probabilité mathématique minimale ne sont pas atteints pour cette catégorie.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ResponsibleBettingNotice() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(PitchSurfaceVariant)
            .border(1.dp, PitchBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = WarningOrange,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Avertissement de transparence : Les paris sportifs comportent des risques d'endettement et d'aléa. Ne misez jamais d'argent dont vous avez besoin pour vos dépenses essentielles.",
                fontSize = 11.sp,
                color = TextMuted,
                lineHeight = 15.sp
            )
        }
    }
}
