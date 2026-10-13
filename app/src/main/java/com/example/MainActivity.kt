package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val matches by viewModel.matches.collectAsStateWithLifecycle()
                val accumulators by viewModel.accumulators.collectAsStateWithLifecycle()
                val historyList by viewModel.history.collectAsStateWithLifecycle(initialValue = emptyList())

                val snackbarHostState = remember { SnackbarHostState() }

                // Gestion des notifications snackbar
                LaunchedEffect(uiState.couponCopiedMessage) {
                    uiState.couponCopiedMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.setCouponCopiedMessage(null)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = PitchDark,
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldNeon.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "⚽", fontSize = 16.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "CarsonPredict AI",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Robot Pronostics Football",
                                            fontSize = 11.sp,
                                            color = EmeraldNeon,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            },
                            actions = {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 12.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (uiState.isAnalyzing) WarningOrange.copy(alpha = 0.2f) else PitchSurfaceVariant)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (uiState.isAnalyzing) WarningOrange else EmeraldNeon)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (uiState.isAnalyzing) "Calcul..." else "IA Active",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (uiState.isAnalyzing) WarningOrange else EmeraldNeon
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = PitchSurface,
                                titleContentColor = TextPrimary
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = PitchSurface,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = uiState.currentTab == AppTab.MATCHS,
                                onClick = { viewModel.selectTab(AppTab.MATCHS) },
                                icon = { Icon(Icons.Default.SportsSoccer, contentDescription = "Matchs") },
                                label = { Text("Matchs", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = EmeraldNeon,
                                    indicatorColor = EmeraldNeon,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_item_matchs")
                            )

                            NavigationBarItem(
                                selected = uiState.currentTab == AppTab.COMBINES,
                                onClick = { viewModel.selectTab(AppTab.COMBINES) },
                                icon = { Icon(Icons.Default.FormatListNumbered, contentDescription = "Combinés") },
                                label = { Text("Combinés", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = GoldTrophy,
                                    indicatorColor = GoldTrophy,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_item_combines")
                            )

                            NavigationBarItem(
                                selected = uiState.currentTab == AppTab.HISTORIQUE,
                                onClick = { viewModel.selectTab(AppTab.HISTORIQUE) },
                                icon = { Icon(Icons.Default.QueryStats, contentDescription = "Historique") },
                                label = { Text("Bilan", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = AccentCyan,
                                    indicatorColor = AccentCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_item_historique")
                            )

                            NavigationBarItem(
                                selected = uiState.currentTab == AppTab.ROBOT_IA,
                                onClick = { viewModel.selectTab(AppTab.ROBOT_IA) },
                                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Robot IA") },
                                label = { Text("Robot IA", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = EmeraldNeon,
                                    indicatorColor = EmeraldNeon,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_item_robot")
                            )

                            NavigationBarItem(
                                selected = uiState.currentTab == AppTab.GUIDE_0_FCFA,
                                onClick = { viewModel.selectTab(AppTab.GUIDE_0_FCFA) },
                                icon = { Icon(Icons.Default.MonetizationOn, contentDescription = "0 FCFA") },
                                label = { Text("0 FCFA", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = GoldTrophy,
                                    indicatorColor = GoldTrophy,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_item_guide")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (uiState.currentTab) {
                            AppTab.MATCHS -> {
                                HomeScreen(
                                    matches = matches,
                                    selectedLeague = uiState.selectedLeague,
                                    onSelectLeague = { viewModel.selectLeague(it) },
                                    onSelectMatch = { viewModel.selectMatch(it) },
                                    isAnalyzing = uiState.isAnalyzing,
                                    lastRefreshTime = uiState.lastRefreshTime,
                                    onRefresh = { viewModel.triggerRobotAnalysis() }
                                )
                            }
                            AppTab.COMBINES -> {
                                AccumulatorsScreen(
                                    accumulators = accumulators,
                                    selectedType = uiState.selectedAccumulatorType,
                                    onSelectType = { viewModel.selectAccumulatorType(it) },
                                    onCouponCopied = { viewModel.setCouponCopiedMessage(it) }
                                )
                            }
                            AppTab.HISTORIQUE -> {
                                HistoryScreen(
                                    historyList = historyList,
                                    currentFilter = uiState.historyFilter,
                                    onFilterChanged = { viewModel.setHistoryFilter(it) }
                                )
                            }
                            AppTab.ROBOT_IA -> {
                                RobotDashboardScreen(
                                    isAnalyzing = uiState.isAnalyzing,
                                    lastRefreshTime = uiState.lastRefreshTime,
                                    onTriggerAnalysis = { viewModel.triggerRobotAnalysis() }
                                )
                            }
                            AppTab.GUIDE_0_FCFA -> {
                                ZeroFcfaGuideScreen()
                            }
                        }

                        // Modal Bottom Sheet pour l'analyse approfondie du match
                        uiState.selectedMatch?.let { match ->
                            MatchDetailSheet(
                                match = match,
                                onDismiss = { viewModel.selectMatch(null) },
                                geminiReport = uiState.geminiAnalysisMap[match.id],
                                isGeminiLoading = uiState.isGeminiLoading,
                                onGenerateAiReport = { viewModel.generateDeepAiReport(match) }
                            )
                        }
                    }
                }
            }
        }
    }
}
