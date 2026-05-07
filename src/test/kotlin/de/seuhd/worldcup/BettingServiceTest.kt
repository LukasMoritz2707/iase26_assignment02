package de.seuhd.worldcup

import de.seuhd.worldcup.BettingService.bets
import org.junit.jupiter.api.assertThrows
/*import java.lang.classfile.Attributes.code*/
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BettingServiceTest {

    private fun match(id: Int, home: String, away: String, hs: Int?, aws: Int?) =
        Match(
            matchId = id,
            round = "Matchday 1",
            date = "2026-06-01",
            homeTeam = home,
            awayTeam = away,
            homeScore = hs,
            awayScore = aws,
            ground = "Test Stadium"
        )

    @BeforeTest
    fun resetBets() {
        BettingService.clear()
    }

    // ── evaluateBonus ──────────────────────────────────────────────────────────

    @Test
    fun `evaluateBonus awards 3 points for an exact score prediction`() {
        val matches = listOf(
            match(1, "AAA", "BBB", 1, 0),
            match(2, "AAA", "BBB", 0, 1)
        )
        BettingService.placeBet(Bet(1, Prediction.HOME_WIN, 1, 0))
        BettingService.placeBet(Bet(2, Prediction.AWAY_WIN, 0, 1))

        assertEquals(6, BettingService.evaluateBonus(matches))

    }

    @Test
    fun `evaluateBonus awards 1 point for correct outcome without exact score`() {
        val matches = listOf(
            match(1, "AAA", "BBB", 0, 0)
        )
        BettingService.placeBet(Bet(1, Prediction.DRAW, 1, 1))

        assertEquals(1, BettingService.evaluateBonus(matches))

    }

    @Test
    fun `evaluateBonus awards 0 points for a wrong prediction`() {
        val matches = listOf(
            match(1, "AAA", "BBB", 0, 1)
        )
        BettingService.placeBet(Bet(1, Prediction.HOME_WIN, 1, 0))

        assertEquals(0, BettingService.evaluateBonus(matches))
    }

    @Test
    fun `evaluateBonus ignores unplayed matches`() {
        val matches = listOf(
            match(1, "AAA", "BBB", 0, 1),
            match(2, "CCC", "DDD", 1, 0),
        )
        BettingService.placeBet(Bet(2, Prediction.HOME_WIN, 1, 0))

        assertEquals(3, BettingService.evaluateBonus(matches))
    }

    // ── removeBet ─────────────────────────────────────────────────────────────

    @Test
    fun `removeBet removes an existing bet so it no longer affects evaluation`() {
        val testMatch = match(1, "AAA", "BBB", hs = null, aws = null)
        BettingService.placeBet(Bet(testMatch.matchId, Prediction.HOME_WIN, null, null))

        BettingService.removeBet(testMatch.matchId)

        assertEquals(0,bets.size)
    }

    @Test
    fun `removeBet does nothing when no bet exists for that matchId`() {
        val testMatch = match(1, "AAA", "BBB", hs = null, aws = null)
        BettingService.placeBet(Bet(testMatch.matchId, Prediction.HOME_WIN, null, null))

        BettingService.removeBet(2)

        assertEquals(1,bets.size)
        assertEquals(testMatch.matchId, bets[1]?.matchId)
        assertEquals(Prediction.HOME_WIN, bets[1]?.prediction)
        assertEquals(testMatch.homeScore, bets[1]?.predictedHomeScore)
        assertEquals(testMatch.awayScore, bets[1]?.predictedAwayScore)
    }

    // ── changeBet ─────────────────────────────────────────────────────────────

    @Test
    fun `changeBet updates the prediction for an existing bet`() {
        val testMatch = match(1, "AAA", "BBB", hs = null, aws = null)
        var bet = Bet(testMatch.matchId, Prediction.HOME_WIN, null, null)
        BettingService.placeBet(bet)

        bet = Bet(testMatch.matchId, Prediction.AWAY_WIN, null, null)

        BettingService.changeBet(bet)

        assertEquals(testMatch.matchId, bets[1]?.matchId)
        assertEquals(Prediction.AWAY_WIN, bets[1]?.prediction)
        assertEquals(testMatch.homeScore, bets[1]?.predictedHomeScore)
        assertEquals(testMatch.awayScore, bets[1]?.predictedAwayScore)
    }

    @Test
    fun `changeBet throws when no bet exists for that matchId`() {
        val testMatch = match(1, "AAA", "BBB", hs = null, aws = null)
        var bet = Bet(testMatch.matchId, Prediction.HOME_WIN, null, null)
        BettingService.placeBet(bet)

        bet = Bet(2, Prediction.HOME_WIN, null, null)

        assertThrows<IllegalArgumentException>{BettingService.changeBet(bet)}
        assertEquals(testMatch.matchId, bets[1]?.matchId)
        assertEquals(Prediction.HOME_WIN, bets[1]?.prediction)
        assertEquals(testMatch.homeScore, bets[1]?.predictedHomeScore)
        assertEquals(testMatch.awayScore, bets[1]?.predictedAwayScore)
    }
}