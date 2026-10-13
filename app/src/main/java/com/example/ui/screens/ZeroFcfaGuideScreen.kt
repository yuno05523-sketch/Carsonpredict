package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ZeroFcfaGuideScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("zero_fcfa_guide_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PitchSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldTrophy))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldTrophy.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = GoldTrophy, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Guide Budget 0 FCFA",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldTrophy
                            )
                            Text(
                                text = "Lancer CarsonPredict AI sans aucun investissement",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Ce guide détaille comment faire fonctionner, héberger et faire évoluer votre application de pronostics avec exactement 0 FCFA de dépenses mensuelles.",
                        fontSize = 13.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Section 1: APIs Sportives & IA 100% Gratuites
        item {
            GuideSectionCard(
                icon = Icons.Default.Storage,
                title = "1. APIs de Données & IA Gratuites",
                color = EmeraldNeon,
                items = listOf(
                    "Football-Data.org (Plan Gratuit) : Offre 10 requêtes/minute gratuites à vie, couvrant la Premier League, Ligue 1, Liga, Serie A, Bundesliga et Champions League sans carte bancaire requise.",
                    "API-Football (RapidAPI Free) : 100 requêtes/jour gratuites. Idéal pour les scores en direct et cotes de base.",
                    "Google AI Studio (Gemini API Free Tier) : Jusqu'à 15 requêtes/minute 100% gratuites pour faire rédiger les analyses tactiques par l'IA.",
                    "Moteur de Poisson Local : Intégré directement dans l'application CarsonPredict. Il calcule les probabilités sur le processeur du téléphone sans aucun coût d'API."
                )
            )
        }

        // Section 2: Méthode de Création Sans Coder (No-Code / Low-Code)
        item {
            GuideSectionCard(
                icon = Icons.Default.Build,
                title = "2. Méthodes de Création Sans Coder",
                color = AccentCyan,
                items = listOf(
                    "FlutterFlow (Offre Free) : Permet de construire des interfaces mobiles par glisser-déposer et de compiler directement un APK.",
                    "Google AppSheet (Gratuit pour usage perso) : Transforme une simple feuille Google Sheets remplie des cotes du jour en application Android en 5 minutes.",
                    "Automatisation n8n ou Make (Free Tier) : Créez un scénario gratuit qui récupère chaque matin les matchs du jour et calcule automatiquement les combinés Cote 3, 4 et 5."
                )
            )
        }

        // Section 3: Hébergement & Déploiement 0 FCFA
        item {
            GuideSectionCard(
                icon = Icons.Default.CloudQueue,
                title = "3. Hébergement & Distribution 0 FCFA",
                color = GoldTrophy,
                items = listOf(
                    "Architecture 'On-Device' (0 FCFA de serveur) : L'application stocke tout dans sa base SQLite / Room locale. Le robot fonctionne sur le téléphone sans louer de serveur VPS !",
                    "Cloudflare Workers (Gratuit) : 100 000 requêtes gratuites par jour pour créer un petit micro-service qui relaie les données des matchs.",
                    "Firebase Spark Plan (Gratuit) : Base Firestore et authentification gratuites jusqu'à 50 000 lectures par jour.",
                    "Distribution sans payer les 25$ du Play Store : Partagez le fichier .APK directement via un canal Telegram, un groupe WhatsApp ou un site GitHub Pages gratuit."
                )
            )
        }

        // Section 4: Limites et Solutions de Contournement
        item {
            GuideSectionCard(
                icon = Icons.Default.Warning,
                title = "4. Limites des Offres Gratuites & Solutions",
                color = WarningOrange,
                items = listOf(
                    "Limite de requêtes API (10 req/min) ➜ Solution : Mettre en cache les matchs du jour dans le téléphone. Une seule requête à 08h00 suffit pour toute la journée !",
                    "Cotes en direct parfois absentes en gratuit ➜ Solution : Le moteur de Poisson de CarsonPredict déduit les cotes équitables directement à partir des probabilités statistiques calculées.",
                    "Ligues locales africaines (Burkina Faso, etc.) peu couvertes par les APIs européennes ➜ Solution : Intégration hybride permettant à l'administrateur d'entrer les résultats de Ligue 1 Faso dans un Google Sheet gratuit lu par l'application."
                )
            )
        }
    }
}

@Composable
fun GuideSectionCard(
    icon: ImageVector,
    title: String,
    color: Color,
    items: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PitchSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.forEach { text ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = "•", color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = text,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}
