package com.example.data.model

data class Team(
    val id: String,
    val name: String,
    val shortName: String,
    val league: String,
    val logoEmoji: String = "⚽",
    val homeRank: Int,
    val awayRank: Int,
    val goalsScoredAvg: Double,
    val goalsConcededAvg: Double,
    val cleanSheetsCount: Int,
    val formLast5: List<MatchOutcome> // V, N, D
)

enum class MatchOutcome {
    VICTOIRE, NUL, DEFAITE
}

data class AbsentPlayer(
    val name: String,
    val position: String,
    val reason: String, // e.g., "Blessure cheville", "Suspension"
    val importance: Importance // MAJEURE, SECONDAIRE
)

enum class Importance {
    MAJEURE, SECONDAIRE
}

data class HeadToHead(
    val date: String,
    val homeScore: Int,
    val awayScore: Int,
    val winner: String
)

data class MatchOdds(
    val homeWin: Double,     // 1
    val draw: Double,        // N
    val awayWin: Double,     // 2
    val over15: Double,
    val under15: Double,
    val over25: Double,
    val under25: Double,
    val over35: Double,
    val under35: Double,
    val bttsYes: Double,     // Les deux équipes marquent Oui
    val bttsNo: Double,      // Les deux équipes marquent Non
    val doubleChance1X: Double,
    val doubleChanceX2: Double,
    val doubleChance12: Double
)

data class MatchProbabilities(
    val homeWinProb: Double,   // 0.0 - 1.0
    val drawProb: Double,
    val awayWinProb: Double,
    val over15Prob: Double,
    val under15Prob: Double,
    val over25Prob: Double,
    val under25Prob: Double,
    val over35Prob: Double,
    val under35Prob: Double,
    val bttsYesProb: Double,
    val bttsNoProb: Double,
    val dc1XProb: Double,
    val dcX2Prob: Double,
    val dc12Prob: Double
)

enum class MatchStatus {
    A_VENIR, EN_DIRECT, TERMINE, REPORTE
}

data class Prediction(
    val recommendedMarket: String, // e.g. "Victoire Domicile", "Plus de 2.5 buts", "1X (Double Chance)"
    val recommendedOdds: Double,
    val confidencePercent: Int,    // 0 - 100
    val tacticalReport: String,     // Explication détaillée du robot
    val keyFactor: String,          // Facteur déterminant
    val riskLevel: RiskLevel
)

enum class RiskLevel {
    FAIBLE, MODERE, ELEVE
}

data class Match(
    val id: String,
    val league: String,
    val leagueFlag: String,
    val matchTime: String,
    val dateStr: String,
    val homeTeam: Team,
    val awayTeam: Team,
    val status: MatchStatus = MatchStatus.A_VENIR,
    val actualScore: Pair<Int, Int>? = null, // for finished matches
    val absentPlayers: List<AbsentPlayer> = emptyList(),
    val h2hHistory: List<HeadToHead> = emptyList(),
    val odds: MatchOdds,
    val probabilities: MatchProbabilities,
    val prediction: Prediction,
    val dataQualityScore: Int = 95 // % de complétude des données
)

enum class AccumulatorType(val title: String, val targetOddsRange: String, val maxRisk: String) {
    COTE_3("Combiné Cote 3 - Sécurité", "2.80 - 3.40", "Risque Minimal (Sécurité Max)"),
    COTE_4("Combiné Cote 4 - Équilibré", "3.70 - 4.50", "Risque Modéré (Value Bet)"),
    COTE_5("Combiné Cote 5 - Audacieux", "4.80 - 5.80", "Risque Élevé (Spéculatif calculé)")
}

data class AccumulatorSelection(
    val matchId: String,
    val matchTitle: String,
    val league: String,
    val market: String,
    val odds: Double,
    val estimatedProbability: Double,
    val reason: String
)

data class Accumulator(
    val id: String,
    val type: AccumulatorType,
    val title: String,
    val date: String,
    val selections: List<AccumulatorSelection>,
    val totalOdds: Double,
    val estimatedCombinedProbability: Double,
    val overallRationale: String,
    val isReliable: Boolean = true,
    val status: PredictionResultStatus = PredictionResultStatus.EN_ATTENTE
)

enum class PredictionResultStatus(val label: String) {
    EN_ATTENTE("En attente"),
    GAGNE("Validé"),
    PERDU("Perdu"),
    REPORTE("Reporté")
}

data class HistoryItem(
    val id: String,
    val date: String,
    val type: String, // "Combiné Cote 3", "Combiné Cote 4", "Combiné Cote 5", "Match Solo"
    val description: String,
    val selectionsSummary: String,
    val totalOdds: Double,
    val status: PredictionResultStatus,
    val netRoiPercentage: Double
)
