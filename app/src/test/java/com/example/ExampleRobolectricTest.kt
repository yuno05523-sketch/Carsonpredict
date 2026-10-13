package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.engine.FootballPredictionEngine
import com.example.data.model.MatchOutcome
import com.example.data.model.Team
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CarsonPredict AI", appName)
    }

    @Test
    fun `test football prediction engine probabilities`() {
        val homeTeam = Team(
            id = "t1",
            name = "Team A",
            shortName = "A",
            league = "Ligue 1",
            homeRank = 1,
            awayRank = 2,
            goalsScoredAvg = 2.4,
            goalsConcededAvg = 0.8,
            cleanSheetsCount = 10,
            formLast5 = listOf(MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.VICTOIRE, MatchOutcome.NUL, MatchOutcome.VICTOIRE)
        )
        val awayTeam = Team(
            id = "t2",
            name = "Team B",
            shortName = "B",
            league = "Ligue 1",
            homeRank = 8,
            awayRank = 10,
            goalsScoredAvg = 1.0,
            goalsConcededAvg = 1.8,
            cleanSheetsCount = 4,
            formLast5 = listOf(MatchOutcome.DEFAITE, MatchOutcome.NUL, MatchOutcome.DEFAITE, MatchOutcome.VICTOIRE, MatchOutcome.DEFAITE)
        )

        val probs = FootballPredictionEngine.calculateProbabilities(homeTeam, awayTeam)
        // Home team should have higher win probability than away team
        assertTrue("Home win prob should be > away win prob", probs.homeWinProb > probs.awayWinProb)
        assertTrue("Sum of 1N2 probabilities should be approx 1.0", (probs.homeWinProb + probs.drawProb + probs.awayWinProb) in 0.98..1.02)
        assertTrue("Over 1.5 prob should be reasonable", probs.over15Prob in 0.1..0.99)
    }
}
