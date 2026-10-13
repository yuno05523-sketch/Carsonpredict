package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.remote.GeminiTacticalService
import com.example.data.repository.MatchRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab {
    MATCHS,
    COMBINES,
    HISTORIQUE,
    ROBOT_IA,
    GUIDE_0_FCFA
}

data class UiState(
    val currentTab: AppTab = AppTab.MATCHS,
    val selectedLeague: String = "Tous",
    val selectedMatch: Match? = null,
    val selectedAccumulatorType: AccumulatorType = AccumulatorType.COTE_3,
    val historyFilter: String = "Tous",
    val isAnalyzing: Boolean = false,
    val lastRefreshTime: Long = System.currentTimeMillis(),
    val geminiAnalysisMap: Map<String, String> = emptyMap(),
    val isGeminiLoading: Boolean = false,
    val couponCopiedMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MatchRepository(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    val matches: StateFlow<List<Match>> = repository.matchesFlow
    val accumulators: StateFlow<List<Accumulator>> = repository.accumulatorsFlow
    val history: Flow<List<HistoryItem>> = repository.getHistory()

    init {
        viewModelScope.launch {
            repository.isAnalyzing.collect { analyzing ->
                _uiState.update { it.copy(isAnalyzing = analyzing) }
            }
        }
        viewModelScope.launch {
            repository.lastRefreshTimestamp.collect { time ->
                _uiState.update { it.copy(lastRefreshTime = time) }
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun selectLeague(league: String) {
        _uiState.update { it.copy(selectedLeague = league) }
    }

    fun selectMatch(match: Match?) {
        _uiState.update { it.copy(selectedMatch = match) }
    }

    fun selectAccumulatorType(type: AccumulatorType) {
        _uiState.update { it.copy(selectedAccumulatorType = type) }
    }

    fun setHistoryFilter(filter: String) {
        _uiState.update { it.copy(historyFilter = filter) }
    }

    fun triggerRobotAnalysis() {
        viewModelScope.launch {
            repository.reanalyzeRobotData()
        }
    }

    fun generateDeepAiReport(match: Match) {
        if (_uiState.value.geminiAnalysisMap.containsKey(match.id)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isGeminiLoading = true) }
            val probsSummary = "1X2(${Math.round(match.probabilities.homeWinProb * 100)}% / ${Math.round(match.probabilities.drawProb * 100)}% / ${Math.round(match.probabilities.awayWinProb * 100)}%), Over 2.5(${Math.round(match.probabilities.over25Prob * 100)}%), BTTS(${Math.round(match.probabilities.bttsYesProb * 100)}%)"
            val absentsSummary = if (match.absentPlayers.isEmpty()) "Aucun absent majeur" else match.absentPlayers.joinToString { "${it.name} (${it.reason})" }

            val result = GeminiTacticalService.generateDeepTacticalReport(
                homeTeam = match.homeTeam.name,
                awayTeam = match.awayTeam.name,
                league = match.league,
                probsSummary = probsSummary,
                absentPlayersSummary = absentsSummary
            )

            _uiState.update {
                val updatedMap = it.geminiAnalysisMap.toMutableMap()
                updatedMap[match.id] = result
                it.copy(
                    geminiAnalysisMap = updatedMap,
                    isGeminiLoading = false
                )
            }
        }
    }

    fun setCouponCopiedMessage(msg: String?) {
        _uiState.update { it.copy(couponCopiedMessage = msg) }
    }
}
