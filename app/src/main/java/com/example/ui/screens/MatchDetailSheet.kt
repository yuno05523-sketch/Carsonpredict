package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
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
import com.example.data.model.Importance
import com.example.data.model.Match
import com.example.ui.components.FormBadge
import com.example.ui.components.ProbabilityBar
import com.example.ui.components.StatComparisonRow
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailSheet(
    match: Match,
    onDismiss: () -> Unit,
    geminiReport: String?,
    isGeminiLoading: Boolean,
    onGenerateAiReport: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = PitchDark,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = PitchBorder)
        },
        modifier = Modifier.testTag("match_detail_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header with match title & close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${match.league} ${match.leagueFlag}",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${match.homeTeam.name} vs ${match.awayTeam.name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Recommendation Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PitchSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldNeon))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldTrophy,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "RECOMMANDATION DU ROBOT IA",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldTrophy
                            )
                        }
                        Text(
                            text = "Confiance ${match.prediction.confidencePercent}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldNeon
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Marché sélectionné :",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = match.prediction.recommendedMarket,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldTrophy)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Cote @${String.format("%.2f", match.prediction.recommendedOdds)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Facteur clé : ${match.prediction.keyFactor}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: Probabilités estimées (Poisson)
            Text(
                text = "1. Probabilités Estimées par l'IA (Poisson)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PitchSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Résultat du Match (1N2)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    ProbabilityBar(
                        label = "1 - Victoire ${match.homeTeam.shortName}",
                        probability = match.probabilities.homeWinProb,
                        odds = match.odds.homeWin,
                        barColor = EmeraldNeon
                    )
                    ProbabilityBar(
                        label = "N - Match Nul",
                        probability = match.probabilities.drawProb,
                        odds = match.odds.draw,
                        barColor = WarningOrange
                    )
                    ProbabilityBar(
                        label = "2 - Victoire ${match.awayTeam.shortName}",
                        probability = match.probabilities.awayWinProb,
                        odds = match.odds.awayWin,
                        barColor = AccentCyan
                    )

                    HorizontalDivider(color = PitchBorder, modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "Total de Buts (Over / Under)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    ProbabilityBar(
                        label = "Plus de 1.5 buts (+1.5)",
                        probability = match.probabilities.over15Prob,
                        odds = match.odds.over15,
                        barColor = EmeraldLight
                    )
                    ProbabilityBar(
                        label = "Plus de 2.5 buts (+2.5)",
                        probability = match.probabilities.over25Prob,
                        odds = match.odds.over25,
                        barColor = EmeraldNeon
                    )
                    ProbabilityBar(
                        label = "Moins de 2.5 buts (-2.5)",
                        probability = match.probabilities.under25Prob,
                        odds = match.odds.under25,
                        barColor = WarningOrange
                    )
                    ProbabilityBar(
                        label = "Plus de 3.5 buts (+3.5)",
                        probability = match.probabilities.over35Prob,
                        odds = match.odds.over35,
                        barColor = GoldTrophy
                    )

                    HorizontalDivider(color = PitchBorder, modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "Marchés Spéciaux (BTTS & Double Chance)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    ProbabilityBar(
                        label = "Les 2 équipes marquent (BTTS OUI)",
                        probability = match.probabilities.bttsYesProb,
                        odds = match.odds.bttsYes,
                        barColor = AccentCyan
                    )
                    ProbabilityBar(
                        label = "Double Chance 1X (${match.homeTeam.shortName} ou Nul)",
                        probability = match.probabilities.dc1XProb,
                        odds = match.odds.doubleChance1X,
                        barColor = EmeraldNeon
                    )
                    ProbabilityBar(
                        label = "Double Chance X2 (Nul ou ${match.awayTeam.shortName})",
                        probability = match.probabilities.dcX2Prob,
                        odds = match.odds.doubleChanceX2,
                        barColor = WarningOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Forme & Statistiques Offensives / Défensives
            Text(
                text = "2. Forme & Statistiques Comparées",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PitchSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Forme 5 derniers matchs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = match.homeTeam.shortName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldNeon)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                match.homeTeam.formLast5.forEach { FormBadge(it) }
                            }
                        }
                        Text(text = "Forme 5 derniers", fontSize = 11.sp, color = TextMuted)
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = match.awayTeam.shortName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                match.awayTeam.formLast5.forEach { FormBadge(it) }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    StatComparisonRow(
                        label = "Moyenne buts marqués/m",
                        homeValue = "${match.homeTeam.goalsScoredAvg}",
                        awayValue = "${match.awayTeam.goalsScoredAvg}",
                        homeRatio = (match.homeTeam.goalsScoredAvg / (match.homeTeam.goalsScoredAvg + match.awayTeam.goalsScoredAvg)).toFloat()
                    )

                    StatComparisonRow(
                        label = "Moyenne buts concédés/m",
                        homeValue = "${match.homeTeam.goalsConcededAvg}",
                        awayValue = "${match.awayTeam.goalsConcededAvg}",
                        homeRatio = (match.homeTeam.goalsConcededAvg / (match.homeTeam.goalsConcededAvg + match.awayTeam.goalsConcededAvg)).toFloat()
                    )

                    StatComparisonRow(
                        label = "Clean sheets (matchs sans but)",
                        homeValue = "${match.homeTeam.cleanSheetsCount}",
                        awayValue = "${match.awayTeam.cleanSheetsCount}",
                        homeRatio = (match.homeTeam.cleanSheetsCount.toFloat() / (match.homeTeam.cleanSheetsCount + match.awayTeam.cleanSheetsCount + 1).toFloat())
                    )

                    StatComparisonRow(
                        label = "Classement Domicile / Extérieur",
                        homeValue = "#${match.homeTeam.homeRank}",
                        awayValue = "#${match.awayTeam.awayRank}",
                        homeRatio = 0.5f
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 3: Confrontations Directes (H2H)
            if (match.h2hHistory.isNotEmpty()) {
                Text(
                    text = "3. Confrontations Directes (H2H)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PitchSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        match.h2hHistory.forEach { h2h ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = h2h.date, fontSize = 12.sp, color = TextMuted)
                                Text(
                                    text = "${match.homeTeam.shortName} ${h2h.homeScore} - ${h2h.awayScore} ${match.awayTeam.shortName}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Gagnant : ${h2h.winner}",
                                    fontSize = 11.sp,
                                    color = if (h2h.winner == "Nul") WarningOrange else EmeraldNeon
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Section 4: Joueurs Absents et Blessures
            Text(
                text = "4. Joueurs Absents & Infirmerie",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PitchSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (match.absentPlayers.isEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Aucun joueur majeur blessé ou suspendu signalé.", fontSize = 12.sp, color = TextSecondary)
                        }
                    } else {
                        match.absentPlayers.forEach { player ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        tint = if (player.importance == Importance.MAJEURE) DangerRed else WarningOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = player.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(text = "${player.position} • ${player.reason}", fontSize = 11.sp, color = TextMuted)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (player.importance == Importance.MAJEURE) DangerRed.copy(alpha = 0.2f) else WarningOrange.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (player.importance == Importance.MAJEURE) "Majeure" else "Mineure",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (player.importance == Importance.MAJEURE) DangerRed else WarningOrange
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 5: Rapport Détaillé Argumenté de l'IA
            Text(
                text = "5. Rapport d'Analyse du Robot IA",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PitchSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = match.prediction.tacticalReport,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        lineHeight = 19.sp
                    )

                    if (geminiReport != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = PitchBorder)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Analyse Complémentaire Gemini AI :", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = geminiReport,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onGenerateAiReport,
                            enabled = !isGeminiLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_deep_ai_report"),
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldNeon))
                        ) {
                            if (isGeminiLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = EmeraldNeon, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Génération du rapport Gemini...", color = EmeraldNeon, fontSize = 12.sp)
                            } else {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Approfondir avec Gemini AI (Optionnel)", color = EmeraldNeon, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
