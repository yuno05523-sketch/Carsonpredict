package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RobotDashboardScreen(
    isAnalyzing: Boolean,
    lastRefreshTime: Long,
    onTriggerAnalysis: () -> Unit
) {
    var autoRefreshEnabled by remember { mutableStateOf(true) }
    var alertMissingDataEnabled by remember { mutableStateOf(true) }
    var strictQualityFilterEnabled by remember { mutableStateOf(true) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRANCE)
    val formattedTimestamp = dateFormat.format(Date(lastRefreshTime))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("robot_dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PitchSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldNeon))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (isAnalyzing) WarningOrange else EmeraldNeon)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAnalyzing) "ROBOT EN COURS D'ANALYSE..." else "ROBOT ACTIF & OPÉRATIONNEL",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isAnalyzing) WarningOrange else EmeraldNeon
                            )
                        }

                        Text(
                            text = "Version 1.0",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Moteur Autonome CarsonPredict",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Exécute en tâche de fond le scan des calendriers, la distribution de Poisson, les confrontations directes et la génération des coupons.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onTriggerAnalysis,
                        enabled = !isAnalyzing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_trigger_robot"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon, contentColor = Color.Black)
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Exécution du scan statistique...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Relancer le cycle d'analyse IA", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Telemetry & Diagnostic
        item {
            Text(
                text = "Télémétrie & Surveillance des Données",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PitchSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TelemetryRow("Dernière analyse complète", formattedTimestamp)
                    TelemetryRow("Modèle prédictif", "Poisson multivarié + Forme + H2H")
                    TelemetryRow("Matchs scannés aujourd'hui", "8 rencontres majeures")
                    TelemetryRow("Matchs reportés détectés", "1 match (ASFA vs EFO - Intempéries)")
                    TelemetryRow("Complétude moyenne données", "94.8% (Qualité optimale)")
                }
            }
        }

        // Autonomous Configuration Toggles
        item {
            Text(
                text = "Paramètres d'Automatisation",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PitchSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingToggleRow(
                        title = "Actualisation automatique quotidienne",
                        description = "Relance l'algorithme tous les matins dès publication des cotes.",
                        checked = autoRefreshEnabled,
                        onCheckedChange = { autoRefreshEnabled = it }
                    )
                    HorizontalDivider(color = PitchBorder)
                    SettingToggleRow(
                        title = "Alerte données manquantes ou reportées",
                        description = "Exclut automatiquement les matchs dont les compositions manquent de fiabilité.",
                        checked = alertMissingDataEnabled,
                        onCheckedChange = { alertMissingDataEnabled = it }
                    )
                    HorizontalDivider(color = PitchBorder)
                    SettingToggleRow(
                        title = "Filtre strict de qualité combiné",
                        description = "Affiche 'Aucun combiné' si la probabilité estimée est inférieure aux seuils.",
                        checked = strictQualityFilterEnabled,
                        onCheckedChange = { strictQualityFilterEnabled = it }
                    )
                }
            }
        }

        // Robot Audit Log
        item {
            Text(
                text = "Journal d'Exécution du Robot",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PitchSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LogItem("17:41", "Scan des flux de données : 8 matchs chargés.", EmeraldNeon)
                    LogItem("17:41", "Calcul des xG et matrices de score de 0 à 6 buts terminé.", EmeraldNeon)
                    LogItem("17:41", "Génération des combinés Cote 3, Cote 4 et Cote 5 réussie.", GoldTrophy)
                    LogItem("17:40", "Alerte report : ASFA Yennenga vs EFO marqué REPORTÉ.", WarningOrange)
                    LogItem("17:39", "Vérification des absences majeures (2 blessures critiques détectées).", TextSecondary)
                }
            }
        }
    }
}

@Composable
fun TelemetryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = TextMuted)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = description, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = EmeraldNeon,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = PitchSurfaceVariant
            )
        )
    }
}

@Composable
fun LogItem(time: String, message: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "[$time]", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = message, fontSize = 12.sp, color = color)
    }
}
