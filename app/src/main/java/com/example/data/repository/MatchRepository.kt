package com.example.data.repository

import android.content.Context
import com.example.data.engine.FootballPredictionEngine
import com.example.data.local.AppDatabase
import com.example.data.local.HistoryEntity
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MatchRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val historyDao = db.historyDao()

    private val _matchesFlow = MutableStateFlow<List<Match>>(emptyList())
    val matchesFlow = _matchesFlow.asStateFlow()

    private val _accumulatorsFlow = MutableStateFlow<List<Accumulator>>(emptyList())
    val accumulatorsFlow = _accumulatorsFlow.asStateFlow()

    private val _lastRefreshTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastRefreshTimestamp = _lastRefreshTimestamp.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing = _isAnalyzing.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val rawMatches = generateVerifiedMatches()
        val analyzedMatches = rawMatches.map { raw ->
            val probs = FootballPredictionEngine.calculateProbabilities(
                raw.homeTeam,
                raw.awayTeam,
                raw.absentPlayers
            )
            val pred = FootballPredictionEngine.generateMatchPrediction(
                raw.homeTeam,
                raw.awayTeam,
                probs,
                raw.odds,
                raw.absentPlayers
            )
            raw.copy(probabilities = probs, prediction = pred)
        }

        _matchesFlow.value = analyzedMatches

        val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())
        val generatedAccs = FootballPredictionEngine.generateAccumulators(analyzedMatches, todayStr)
        _accumulatorsFlow.value = generatedAccs
    }

    suspend fun reanalyzeRobotData() = withContext(Dispatchers.IO) {
        _isAnalyzing.value = true
        kotlinx.coroutines.delay(1200) // Simulation d'exécution du robot d'analyse

        val current = _matchesFlow.value
        val updated = current.map { m ->
            // Simule de légères variations ou détection de composition actualisée
            val updatedQuality = (m.dataQualityScore + (0..4).random()).coerceAtMost(100)
            val probs = FootballPredictionEngine.calculateProbabilities(
                m.homeTeam,
                m.awayTeam,
                m.absentPlayers
            )
            val pred = FootballPredictionEngine.generateMatchPrediction(
                m.homeTeam,
                m.awayTeam,
                probs,
                m.odds,
                m.absentPlayers
            )
            m.copy(probabilities = probs, prediction = pred, dataQualityScore = updatedQuality)
        }

        _matchesFlow.value = updated
        val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())
        _accumulatorsFlow.value = FootballPredictionEngine.generateAccumulators(updated, todayStr)
        _lastRefreshTimestamp.value = System.currentTimeMillis()
        _isAnalyzing.value = false
    }

    fun getHistory(): Flow<List<HistoryItem>> {
        return historyDao.getAllHistory().map { entities ->
            if (entities.isEmpty()) {
                // Seed initial history
                val initialList = getInitialHistorySeed()
                historyDao.insertAll(initialList.map {
                    HistoryEntity(
                        id = it.id,
                        date = it.date,
                        type = it.type,
                        description = it.description,
                        selectionsSummary = it.selectionsSummary,
                        totalOdds = it.totalOdds,
                        status = it.status.name,
                        netRoiPercentage = it.netRoiPercentage
                    )
                })
                initialList
            } else {
                entities.map { entity ->
                    HistoryItem(
                        id = entity.id,
                        date = entity.date,
                        type = entity.type,
                        description = entity.description,
                        selectionsSummary = entity.selectionsSummary,
                        totalOdds = entity.totalOdds,
                        status = try {
                            PredictionResultStatus.valueOf(entity.status)
                        } catch (e: Exception) {
                            PredictionResultStatus.EN_ATTENTE
                        },
                        netRoiPercentage = entity.netRoiPercentage
                    )
                }
            }
        }
    }

    private fun getInitialHistorySeed(): List<HistoryItem> {
        return listOf(
            HistoryItem(
                id = "hist_1",
                date = "Hier (11/10/2026)",
                type = "Combiné Cote 3",
                description = "Sélection Sécurité Validée (3/3)",
                selectionsSummary = "Arsenal 1X (1.28) + PSG Plus de 1.5 buts (1.25) + Real Madrid 1X (1.26)",
                totalOdds = 2.02,
                status = PredictionResultStatus.GAGNE,
                netRoiPercentage = 102.0
            ),
            HistoryItem(
                id = "hist_2",
                date = "Hier (11/10/2026)",
                type = "Combiné Cote 4",
                description = "Combiné Équilibré Gagné (2/2)",
                selectionsSummary = "Bayern Munich Victoire (1.62) + Man City vs Chelsea Over 2.5 (1.58) + Lyon 1X (1.45)",
                totalOdds = 3.71,
                status = PredictionResultStatus.GAGNE,
                netRoiPercentage = 271.0
            ),
            HistoryItem(
                id = "hist_3",
                date = "10/10/2026",
                type = "Combiné Cote 5",
                description = "Spéculatif Audacieux Perdu (2/3)",
                selectionsSummary = "Dortmund BTTS (1.75) + Milan AC Victoire (2.10) + Marseille Over 2.5 (1.80)",
                totalOdds = 5.25,
                status = PredictionResultStatus.PERDU,
                netRoiPercentage = -100.0
            ),
            HistoryItem(
                id = "hist_4",
                date = "09/10/2026",
                type = "Combiné Cote 3",
                description = "Sélection Sécurité Validée (3/3)",
                selectionsSummary = "Barcelone Victoire (1.40) + Inter Milan 1X (1.25) + Salitas FC 1X (1.30)",
                totalOdds = 2.27,
                status = PredictionResultStatus.GAGNE,
                netRoiPercentage = 127.0
            ),
            HistoryItem(
                id = "hist_5",
                date = "08/10/2026",
                type = "Combiné Cote 4",
                description = "Équilibré Validé (3/3)",
                selectionsSummary = "Liverpool Victoire (1.55) + Juventus vs Roma Under 2.5 (1.70) + Monaco Over 1.5 (1.35)",
                totalOdds = 3.55,
                status = PredictionResultStatus.GAGNE,
                netRoiPercentage = 255.0
            ),
            HistoryItem(
                id = "hist_6",
                date = "07/10/2026",
                type = "Match Solo",
                description = "Match Reporté pour intempéries",
                selectionsSummary = "ASFA Yennenga vs EFO Ouagadougou (Ligue 1 Burkina)",
                totalOdds = 1.95,
                status = PredictionResultStatus.REPORTE,
                netRoiPercentage = 0.0
            )
        )
    }

    private fun generateVerifiedMatches(): List<Match> {
        val dummyProbs = MatchProbabilities(
            0.5, 0.25, 0.25, 0.7, 0.3, 0.5, 0.5, 0.3, 0.7, 0.55, 0.45, 0.75, 0.5, 0.75
        )
        val dummyPred = Prediction("1X", 1.30, 75, "", "", RiskLevel.FAIBLE)

        return listOf(
            Match(
                id = "m1",
                league = "Premier League",
                leagueFlag = "🇬🇧",
                matchTime = "20:00",
                dateStr = "Aujourd'hui",
                homeTeam = Team(
                    id = "ars",
                    name = "Arsenal FC",
                    shortName = "Arsenal",
                    league = "Premier League",
                    homeRank = 2,
                    awayRank = 3,
                    goalsScoredAvg = 2.1,
                    goalsConcededAvg = 0.8,
                    cleanSheetsCount = 12,
                    formLast5 = listOf(MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.NUL, MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE)
                ),
                awayTeam = Team(
                    id = "che",
                    name = "Chelsea FC",
                    shortName = "Chelsea",
                    league = "Premier League",
                    homeRank = 5,
                    awayRank = 6,
                    goalsScoredAvg = 1.6,
                    goalsConcededAvg = 1.4,
                    cleanSheetsCount = 6,
                    formLast5 = listOf(MatchOutcome.VICTOIRE, MatchOutcome.DEFAITE, MatchOutcome.NUL, MatchOutcome.VICTOIRE, MatchOutcome.DEFAITE)
                ),
                absentPlayers = listOf(
                    AbsentPlayer("Reece James", "Défenseur", "Lésion musculaire", Importance.MAJEURE),
                    AbsentPlayer("Romeo Lavia", "Milieu", "Reprise physique", Importance.SECONDAIRE)
                ),
                h2hHistory = listOf(
                    HeadToHead("23/04/2024", 5, 0, "Arsenal"),
                    HeadToHead("21/10/2023", 2, 2, "Nul"),
                    HeadToHead("02/05/2023", 3, 1, "Arsenal")
                ),
                odds = MatchOdds(
                    homeWin = 1.62, draw = 4.10, awayWin = 5.20,
                    over15 = 1.22, under15 = 4.20,
                    over25 = 1.68, under25 = 2.15,
                    over35 = 2.70, under35 = 1.45,
                    bttsYes = 1.70, bttsNo = 2.05,
                    doubleChance1X = 1.20, doubleChanceX2 = 2.25, doubleChance12 = 1.22
                ),
                probabilities = dummyProbs,
                prediction = dummyPred,
                dataQualityScore = 98
            ),
            Match(
                id = "m2",
                league = "Ligue 1",
                leagueFlag = "🇫🇷",
                matchTime = "21:00",
                dateStr = "Aujourd'hui",
                homeTeam = Team(
                    id = "psg",
                    name = "Paris Saint-Germain",
                    shortName = "PSG",
                    league = "Ligue 1",
                    homeRank = 1,
                    awayRank = 1,
                    goalsScoredAvg = 2.6,
                    goalsConcededAvg = 0.9,
                    cleanSheetsCount = 10,
                    formLast5 = listOf(MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.NUL, MatchOutcome.VICTOIRE)
                ),
                awayTeam = Team(
                    id = "om",
                    name = "Olympique de Marseille",
                    shortName = "Marseille",
                    league = "Ligue 1",
                    homeRank = 3,
                    awayRank = 4,
                    goalsScoredAvg = 1.8,
                    goalsConcededAvg = 1.3,
                    cleanSheetsCount = 7,
                    formLast5 = listOf(MatchOutcome.VICTOIRE, MatchOutcome.NUL, MatchOutcome.VICTOIRE, MatchOutcome.DEFAITE, MatchOutcome.VICTOIRE)
                ),
                absentPlayers = listOf(
                    AbsentPlayer("Presnel Kimpembe", "Défenseur", "Phase de réathlétisation", Importance.SECONDAIRE),
                    AbsentPlayer("Valentin Rongier", "Milieu", "Gêne au genou", Importance.MAJEURE)
                ),
                h2hHistory = listOf(
                    HeadToHead("31/03/2024", 0, 2, "PSG"),
                    HeadToHead("24/09/2023", 4, 0, "PSG"),
                    HeadToHead("26/02/2023", 0, 3, "PSG")
                ),
                odds = MatchOdds(
                    homeWin = 1.55, draw = 4.40, awayWin = 5.50,
                    over15 = 1.18, under15 = 4.80,
                    over25 = 1.58, under25 = 2.30,
                    over35 = 2.45, under35 = 1.52,
                    bttsYes = 1.65, bttsNo = 2.15,
                    doubleChance1X = 1.17, doubleChanceX2 = 2.40, doubleChance12 = 1.19
                ),
                probabilities = dummyProbs,
                prediction = dummyPred,
                dataQualityScore = 96
            ),
            Match(
                id = "m3",
                league = "La Liga",
                leagueFlag = "🇪🇸",
                matchTime = "18:30",
                dateStr = "Aujourd'hui",
                homeTeam = Team(
                    id = "rma",
                    name = "Real Madrid",
                    shortName = "Real Madrid",
                    league = "La Liga",
                    homeRank = 1,
                    awayRank = 2,
                    goalsScoredAvg = 2.3,
                    goalsConcededAvg = 0.7,
                    cleanSheetsCount = 14,
                    formLast5 = listOf(MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.NUL)
                ),
                awayTeam = Team(
                    id = "sev",
                    name = "Sevilla FC",
                    shortName = "Sevilla",
                    league = "La Liga",
                    homeRank = 11,
                    awayRank = 12,
                    goalsScoredAvg = 1.1,
                    goalsConcededAvg = 1.6,
                    cleanSheetsCount = 5,
                    formLast5 = listOf(MatchOutcome.DEFAITE, MatchOutcome.NUL, MatchOutcome.VICTOIRE, MatchOutcome.DEFAITE, MatchOutcome.NUL)
                ),
                absentPlayers = listOf(
                    AbsentPlayer("David Alaba", "Défenseur", "Convalescence", Importance.SECONDAIRE)
                ),
                h2hHistory = listOf(
                    HeadToHead("25/02/2024", 1, 0, "Real Madrid"),
                    HeadToHead("21/10/2023", 1, 1, "Nul"),
                    HeadToHead("27/05/2023", 1, 2, "Real Madrid")
                ),
                odds = MatchOdds(
                    homeWin = 1.35, draw = 5.20, awayWin = 8.50,
                    over15 = 1.16, under15 = 5.10,
                    over25 = 1.52, under25 = 2.45,
                    over35 = 2.30, under35 = 1.60,
                    bttsYes = 1.85, bttsNo = 1.90,
                    doubleChance1X = 1.10, doubleChanceX2 = 3.10, doubleChance12 = 1.15
                ),
                probabilities = dummyProbs,
                prediction = dummyPred,
                dataQualityScore = 95
            ),
            Match(
                id = "m4",
                league = "Bundesliga",
                leagueFlag = "🇩🇪",
                matchTime = "15:30",
                dateStr = "Aujourd'hui",
                homeTeam = Team(
                    id = "bay",
                    name = "Bayern Munich",
                    shortName = "Bayern",
                    league = "Bundesliga",
                    homeRank = 1,
                    awayRank = 1,
                    goalsScoredAvg = 2.9,
                    goalsConcededAvg = 1.1,
                    cleanSheetsCount = 9,
                    formLast5 = listOf(MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.DEFAITE, MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE)
                ),
                awayTeam = Team(
                    id = "bvb",
                    name = "Borussia Dortmund",
                    shortName = "Dortmund",
                    league = "Bundesliga",
                    homeRank = 4,
                    awayRank = 5,
                    goalsScoredAvg = 2.0,
                    goalsConcededAvg = 1.5,
                    cleanSheetsCount = 6,
                    formLast5 = listOf(MatchOutcome.VICTOIRE, MatchOutcome.NUL, MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.DEFAITE)
                ),
                absentPlayers = listOf(
                    AbsentPlayer("Alphonso Davies", "Défenseur", "Avertissement", Importance.SECONDAIRE),
                    AbsentPlayer("Karim Adeyemi", "Attaquant", "Élongation cuisse", Importance.MAJEURE)
                ),
                h2hHistory = listOf(
                    HeadToHead("30/03/2024", 0, 2, "Dortmund"),
                    HeadToHead("04/11/2023", 0, 4, "Bayern"),
                    HeadToHead("01/04/2023", 4, 2, "Bayern")
                ),
                odds = MatchOdds(
                    homeWin = 1.68, draw = 4.30, awayWin = 4.40,
                    over15 = 1.12, under15 = 6.00,
                    over25 = 1.40, under25 = 2.85,
                    over35 = 2.00, under35 = 1.80,
                    bttsYes = 1.45, bttsNo = 2.65,
                    doubleChance1X = 1.22, doubleChanceX2 = 2.15, doubleChance12 = 1.20
                ),
                probabilities = dummyProbs,
                prediction = dummyPred,
                dataQualityScore = 97
            ),
            Match(
                id = "m5",
                league = "Serie A",
                leagueFlag = "🇮🇹",
                matchTime = "20:45",
                dateStr = "Aujourd'hui",
                homeTeam = Team(
                    id = "int",
                    name = "Inter Milan",
                    shortName = "Inter",
                    league = "Serie A",
                    homeRank = 1,
                    awayRank = 1,
                    goalsScoredAvg = 2.2,
                    goalsConcededAvg = 0.6,
                    cleanSheetsCount = 15,
                    formLast5 = listOf(MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.NUL, MatchOutcome.VICTOIRE)
                ),
                awayTeam = Team(
                    id = "ata",
                    name = "Atalanta Bergame",
                    shortName = "Atalanta",
                    league = "Serie A",
                    homeRank = 4,
                    awayRank = 3,
                    goalsScoredAvg = 1.9,
                    goalsConcededAvg = 1.2,
                    cleanSheetsCount = 8,
                    formLast5 = listOf(MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.NUL, MatchOutcome.DEFAITE, MatchOutcome.VICTOIRE)
                ),
                absentPlayers = listOf(
                    AbsentPlayer("Giorgio Scalvini", "Défenseur", "Entorse ligament", Importance.MAJEURE)
                ),
                h2hHistory = listOf(
                    HeadToHead("28/02/2024", 4, 0, "Inter"),
                    HeadToHead("04/11/2023", 1, 2, "Inter"),
                    HeadToHead("27/05/2023", 3, 2, "Inter")
                ),
                odds = MatchOdds(
                    homeWin = 1.75, draw = 3.90, awayWin = 4.50,
                    over15 = 1.22, under15 = 4.10,
                    over25 = 1.70, under25 = 2.10,
                    over35 = 2.75, under35 = 1.42,
                    bttsYes = 1.68, bttsNo = 2.10,
                    doubleChance1X = 1.24, doubleChanceX2 = 2.05, doubleChance12 = 1.25
                ),
                probabilities = dummyProbs,
                prediction = dummyPred,
                dataQualityScore = 93
            ),
            Match(
                id = "m6",
                league = "Ligue 1 Faso",
                leagueFlag = "🇧🇫",
                matchTime = "16:00",
                dateStr = "Aujourd'hui",
                homeTeam = Team(
                    id = "asfa",
                    name = "ASFA Yennenga",
                    shortName = "ASFA",
                    league = "Ligue 1 Faso",
                    homeRank = 2,
                    awayRank = 3,
                    goalsScoredAvg = 1.5,
                    goalsConcededAvg = 0.7,
                    cleanSheetsCount = 11,
                    formLast5 = listOf(MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.NUL, MatchOutcome.VICTOIRE, MatchOutcome.DEFAITE)
                ),
                awayTeam = Team(
                    id = "efo",
                    name = "Étoile Filante de Ouaga",
                    shortName = "EFO",
                    league = "Ligue 1 Faso",
                    homeRank = 4,
                    awayRank = 5,
                    goalsScoredAvg = 1.2,
                    goalsConcededAvg = 0.9,
                    cleanSheetsCount = 9,
                    formLast5 = listOf(MatchOutcome.NUL, MatchOutcome.VICTOIRE, MatchOutcome.NUL, MatchOutcome.DEFAITE, MatchOutcome.VICTOIRE)
                ),
                absentPlayers = listOf(
                    AbsentPlayer("Yacouba Traoré", "Attaquant", "Blessure cheville", Importance.MAJEURE)
                ),
                h2hHistory = listOf(
                    HeadToHead("12/01/2024", 1, 0, "ASFA"),
                    HeadToHead("18/05/2023", 1, 1, "Nul"),
                    HeadToHead("04/12/2022", 0, 0, "Nul")
                ),
                odds = MatchOdds(
                    homeWin = 2.05, draw = 3.00, awayWin = 3.75,
                    over15 = 1.42, under15 = 2.70,
                    over25 = 2.35, under25 = 1.55,
                    over35 = 4.20, under35 = 1.20,
                    bttsYes = 2.10, bttsNo = 1.65,
                    doubleChance1X = 1.28, doubleChanceX2 = 1.70, doubleChance12 = 1.35
                ),
                probabilities = dummyProbs,
                prediction = dummyPred,
                dataQualityScore = 88
            ),
            Match(
                id = "m7",
                league = "CAF Champions League",
                leagueFlag = "🌍",
                matchTime = "17:00",
                dateStr = "Aujourd'hui",
                homeTeam = Team(
                    id = "ahl",
                    name = "Al Ahly SC",
                    shortName = "Al Ahly",
                    league = "CAF Champions League",
                    homeRank = 1,
                    awayRank = 1,
                    goalsScoredAvg = 2.1,
                    goalsConcededAvg = 0.5,
                    cleanSheetsCount = 14,
                    formLast5 = listOf(MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.NUL, MatchOutcome.VICTOIRE)
                ),
                awayTeam = Team(
                    id = "sun",
                    name = "Mamelodi Sundowns",
                    shortName = "Sundowns",
                    league = "CAF Champions League",
                    homeRank = 2,
                    awayRank = 2,
                    goalsScoredAvg = 1.7,
                    goalsConcededAvg = 0.8,
                    cleanSheetsCount = 10,
                    formLast5 = listOf(MatchOutcome.VICTOIRE, MatchOutcome.NUL, MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.DEFAITE)
                ),
                absentPlayers = emptyList(),
                h2hHistory = listOf(
                    HeadToHead("01/11/2023", 0, 0, "Nul"),
                    HeadToHead("29/10/2023", 1, 0, "Sundowns"),
                    HeadToHead("11/03/2023", 5, 2, "Sundowns")
                ),
                odds = MatchOdds(
                    homeWin = 1.90, draw = 3.20, awayWin = 4.20,
                    over15 = 1.35, under15 = 3.00,
                    over25 = 2.15, under25 = 1.65,
                    over35 = 3.80, under35 = 1.24,
                    bttsYes = 1.95, bttsNo = 1.80,
                    doubleChance1X = 1.25, doubleChanceX2 = 1.85, doubleChance12 = 1.32
                ),
                probabilities = dummyProbs,
                prediction = dummyPred,
                dataQualityScore = 92
            ),
            Match(
                id = "m8",
                league = "Premier League",
                leagueFlag = "🇬🇧",
                matchTime = "17:30",
                dateStr = "Aujourd'hui",
                homeTeam = Team(
                    id = "liv",
                    name = "Liverpool FC",
                    shortName = "Liverpool",
                    league = "Premier League",
                    homeRank = 1,
                    awayRank = 1,
                    goalsScoredAvg = 2.4,
                    goalsConcededAvg = 0.9,
                    cleanSheetsCount = 11,
                    formLast5 = listOf(MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.NUL, MatchOutcome.VICTOIRE)
                ),
                awayTeam = Team(
                    id = "avl",
                    name = "Aston Villa",
                    shortName = "Aston Villa",
                    league = "Premier League",
                    homeRank = 4,
                    awayRank = 5,
                    goalsScoredAvg = 1.8,
                    goalsConcededAvg = 1.4,
                    cleanSheetsCount = 7,
                    formLast5 = listOf(MatchOutcome.VICTOIRE, MatchOutcome.DEFAITE, MatchOutcome.NUL, MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE)
                ),
                absentPlayers = listOf(
                    AbsentPlayer("Boubacar Kamara", "Milieu", "Rétablissement", Importance.MAJEURE)
                ),
                h2hHistory = listOf(
                    HeadToHead("13/05/2024", 3, 3, "Nul"),
                    HeadToHead("03/09/2023", 3, 0, "Liverpool"),
                    HeadToHead("20/05/2023", 1, 1, "Nul")
                ),
                odds = MatchOdds(
                    homeWin = 1.50, draw = 4.60, awayWin = 5.80,
                    over15 = 1.15, under15 = 5.50,
                    over25 = 1.48, under25 = 2.60,
                    over35 = 2.20, under35 = 1.65,
                    bttsYes = 1.55, bttsNo = 2.35,
                    doubleChance1X = 1.15, doubleChanceX2 = 2.50, doubleChance12 = 1.18
                ),
                probabilities = dummyProbs,
                prediction = dummyPred,
                dataQualityScore = 96
            )
        )
    }
}
