package com.codewithcled.fullstack_backend_proj1.UnitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Scoreboard;
import com.codewithcled.fullstack_backend_proj1.model.ScoreboardEntry;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.repository.RoundRepository;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.service.EloRatingService;
import com.codewithcled.fullstack_backend_proj1.service.MatchServiceImplementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {
    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private RoundRepository roundRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EloRatingService eloRatingService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    MatchServiceImplementation matchService;

    @Test
    public void createMatch_Success() {
        Double elo1 = (double) 1000;
        Double elo2 = (double) 1010;
        long uId1 = (long) 1;
        long uId2 = (long) 2;

        User p1 = new User();
        p1.setElo(elo1);
        p1.setId(uId1);

        User p2 = new User();
        p2.setElo(elo2);
        p2.setId(uId2);

        Match result = matchService.createMatch(p1, p2);

        assertEquals(uId1, result.getPlayer1());
        assertEquals(uId2, result.getPlayer2());
        assertEquals(elo1, result.getPlayer1StartingElo());
        assertEquals(elo2, result.getPlayer2StartingElo());

    }

    @Test
    void updateRoundScoreboard_Success_Draw() throws Exception {
        // Arrange
        Round testRound = new Round();
        Match testMatch = new Match();
        Long uId1 = (long) 1;
        Long uId2 = uId1 + 1;
        User testPlayer1 = new User();
        User testPlayer2 = new User();
        testPlayer1.setId(uId1);
        testPlayer1.setElo((double) 100);
        testPlayer2.setId(uId2);
        testPlayer2.setElo((double) 200);
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);

        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);

        testRound.setScoreboard(scoreboard);
        Tournament tournament = new Tournament();
        testRound.setTournament(tournament);
        tournament.setRounds(List.of(testRound));

        when(userRepository.findById(uId1)).thenReturn(Optional.of(testPlayer1));
        when(userRepository.findById(uId2)).thenReturn(Optional.of(testPlayer2));
        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                .thenReturn(testMatch);
        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2))
                .thenReturn(testMatch);

        // Act
        matchService.updateRoundScoreboard(testRound, testMatch, 0);

        // Assert
        assertEquals(0.5, scoreboard.getPlayerScore(uId1));
        assertEquals(0.5, scoreboard.getPlayerScore(uId2));
        verify(roundRepository).save(testRound);
    }

    @Test
    void updateRoundScoreboard_Success_Player1Win() throws Exception {
        // Arrange
        Round testRound = new Round();
        Match testMatch = new Match();
        Long uId1 = (long) 1;
        Long uId2 = uId1 + 1;
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);

        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);

        testRound.setScoreboard(scoreboard);
        Tournament tournament = new Tournament();
        testRound.setTournament(tournament);
        tournament.setRounds(List.of(testRound));

        // Act
        matchService.updateRoundScoreboard(testRound, testMatch, -1);

        // Assert
        assertEquals(1.0, scoreboard.getPlayerScore(uId1));
        assertEquals(0.0, scoreboard.getPlayerScore(uId2));
        verify(roundRepository).save(testRound);
    }

    @Test
    void updateRoundScoreboard_Success_Player2Win() throws Exception {
        // Arrange
        Round testRound = new Round();
        Match testMatch = new Match();
        Long uId1 = (long) 1;
        Long uId2 = uId1 + 1;
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);

        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);

        testRound.setScoreboard(scoreboard);

        Tournament tournament = new Tournament();
        testRound.setTournament(tournament);
        tournament.setRounds(List.of(testRound));

        // Act
        matchService.updateRoundScoreboard(testRound, testMatch, 1);

        // Assert
        assertEquals(1.0, scoreboard.getPlayerScore(uId2));
        assertEquals(0.0, scoreboard.getPlayerScore(uId1)); // Player 1 should not have scored
        verify(roundRepository).save(testRound);
    }

    @Test
    void updateRoundScoreboard_Failure_Player1MissingFromScoreboard() throws Exception {
        // Arrange
        Round testRound = new Round();
        Match testMatch = new Match();
        Long uId1 = (long) 1;
        Long uId2 = uId1 + 1;
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);

        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(uId1 + 3, 0.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);

        testRound.setScoreboard(scoreboard);

        Tournament tournament = new Tournament();
        testRound.setTournament(tournament);
        tournament.setRounds(List.of(testRound));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            matchService.updateRoundScoreboard(testRound, testMatch, 1);
        });

        // Assert
        assertEquals("Player not found in scoreboard",exception.getMessage());

    }

    @Test
    void updateRoundScoreboard_Failure_Player2MissingFromScoreboard() throws Exception {
        // Arrange
        Round testRound = new Round();
        Match testMatch = new Match();
        Long uId1 = (long) 1;
        Long uId2 = uId1 + 1;
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);

        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(uId2+3, 0.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);

        testRound.setScoreboard(scoreboard);

        Tournament tournament = new Tournament();
        testRound.setTournament(tournament);
        tournament.setRounds(List.of(testRound));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            matchService.updateRoundScoreboard(testRound, testMatch, 1);
        });

        // Assert
        assertEquals("Player not found in scoreboard",exception.getMessage());

    }

    @Test
    void getResult_Success() throws Exception {
        Match testMatch = new Match();
        long mId = (long) 1;
        int resultTest = 1;
        testMatch.setResult(resultTest);
        testMatch.setId(mId);
        when(matchRepository.findById(mId)).thenReturn(Optional.of(testMatch));

        int result = matchService.getResult(mId);

        assertEquals(resultTest, result);
        verify(matchRepository).findById(mId);
    }

    @Test
    void getResult_Failure() throws Exception {
        long mId = (long) 1;

        when(matchRepository.findById(mId)).thenReturn(Optional.empty());
        boolean exceptionThrown = false;
        try {
            matchService.getResult(mId);
        } catch (Exception e) {
            assertEquals("Match not found", e.getMessage());
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
        verify(matchRepository).findById(mId);
    }

    @Test
    void updateMatch_Success() throws Exception {

        Long mId = (long) 1432;
        int result = 0;
        Double elo = (double) 1000;

        Tournament testTournament = new Tournament();
        testTournament.setNoOfRounds(1);

        Round testRound = new Round();
        testRound.setRoundNum(1);
        testRound.setTournament(testTournament);

        Match testMatch = new Match();
        testMatch.setIsComplete(false);
        testMatch.setRound(testRound);

        User player1 = new User();
        player1.setId(mId);
        player1.setElo(elo);
        testMatch.setPlayer1(player1.getId());
        testMatch.setPlayer1StartingElo(player1.getElo());

        User player2 = new User();
        player2.setId(mId + 1);
        player2.setElo(elo);
        testMatch.setPlayer2(player2.getId());
        testMatch.setPlayer2StartingElo(player2.getElo());

        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(player1.getId(), 1.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(player2.getId(), 0.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);
        testRound.setScoreboard(scoreboard);


        when(matchRepository.findById(mId)).thenReturn(Optional.of(testMatch));
        when(userRepository.findById(mId)).thenReturn(Optional.of(player1));
        when(userRepository.findById(mId + 1)).thenReturn(Optional.of(player2));
        when(eloRatingService.eloRatingForBoth(elo.intValue(), elo.intValue(), result)).thenReturn(List.of(elo, elo));
        when(restTemplate.getForObject("/r/round/" + testRound.getId() + "/checkComplete", String.class))
                .thenReturn(null);
        when(roundRepository.save(any(Round.class))).thenAnswer(invocation -> {
            // Capture the round and return it
            return invocation.getArgument(0);
        });

        matchService.updateMatch(mId, result);

        verify(matchRepository).findById(mId);
        verify(userRepository, times(2)).findById(mId);
        verify(userRepository, times(2)).findById(mId + 1);
    }

    @Test
    void updateMatch_Failure_MatchNotFound() throws Exception {
        Long mId = (long) 1432;
        int result = 0;

        when(matchRepository.findById(mId)).thenReturn(Optional.empty());

        boolean exceptionThrown = false;
        try {
            matchService.updateMatch(mId, result);
        } catch (Exception e) {
            assertEquals("Match not found", e.getMessage());
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
        verify(matchRepository).findById(mId);
    }

    @Test
    void updateMatch_Failure_MatchIsComplete() throws Exception {
        Long mId = (long) 1432;
        int result = 0;

        Match testMatch = new Match();
        testMatch.setIsComplete(true);

        when(matchRepository.findById(mId)).thenReturn(Optional.of(testMatch));

        boolean exceptionThrown = false;
        try {
            matchService.updateMatch(mId, result);
        } catch (Exception e) {
            assertEquals("Match already complete, cannot update again", e.getMessage());
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
        verify(matchRepository).findById(mId);
    }

    @Test
    void updateMatch_Failure_Player1NotFound() throws Exception {
        Long mId = (long) 1432;
        int result = 0;

        Match testMatch = new Match();
        testMatch.setIsComplete(false);

        testMatch.setPlayer1(mId);

        when(matchRepository.findById(mId)).thenReturn(Optional.of(testMatch));
        when(userRepository.findById(mId)).thenReturn(Optional.empty());

        boolean exceptionThrown = false;
        try {
            matchService.updateMatch(mId, result);
        } catch (Exception e) {
            assertEquals("User not found", e.getMessage());
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
        verify(matchRepository).findById(mId);
        verify(userRepository).findById(mId);
    }

    @Test
    void updateMatch_Failure_Player2NotFound() throws Exception {
        Long mId = (long) 1432;
        int result = 0;

        Match testMatch = new Match();
        testMatch.setIsComplete(false);

        User player1 = new User();
        testMatch.setPlayer1(mId);
        testMatch.setPlayer2(mId + 1);

        when(matchRepository.findById(mId)).thenReturn(Optional.of(testMatch));
        when(userRepository.findById(mId)).thenReturn(Optional.of(player1));
        when(userRepository.findById(mId + 1)).thenReturn(Optional.empty());

        boolean exceptionThrown = false;
        try {
            matchService.updateMatch(mId, result);
        } catch (Exception e) {
            assertEquals("User not found", e.getMessage());
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
        verify(matchRepository).findById(mId);
        verify(userRepository).findById(mId);
        verify(userRepository).findById(mId + 1);
    }

    @Test
    void getEloChange1_Success() throws Exception {
        Match testMatch = new Match();
        long mId = (long) 1;
        double eloChange = 10.0;
        testMatch.setEloChange1(eloChange);
        testMatch.setId(mId);

        when(matchRepository.findById(mId)).thenReturn(Optional.of(testMatch));

        double result = matchService.getEloChange1(mId);

        assertEquals(eloChange, result);
        verify(matchRepository).findById(mId);
    }

    @Test
    void getEloChange1_Failure() throws Exception {
        long mId = (long) 1;

        when(matchRepository.findById(mId)).thenReturn(Optional.empty());
        boolean exceptionThrown = false;
        try {
            matchService.getEloChange1(mId);
        } catch (Exception e) {
            assertEquals("Match not found", e.getMessage());
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
        verify(matchRepository).findById(mId);
    }

    @Test
    void getEloChange2_Success() throws Exception {
        Match testMatch = new Match();
        long mId = (long) 1;
        double eloChange = 10.0;
        testMatch.setEloChange2(eloChange);
        testMatch.setId(mId);

        when(matchRepository.findById(mId)).thenReturn(Optional.of(testMatch));

        double result = matchService.getEloChange2(mId);

        assertEquals(eloChange, result);
        verify(matchRepository).findById(mId);
    }

    @Test
    void getEloChange2_Failure() throws Exception {
        long mId = (long) 1;

        when(matchRepository.findById(mId)).thenReturn(Optional.empty());
        boolean exceptionThrown = false;
        try {
            matchService.getEloChange2(mId);
        } catch (Exception e) {
            assertEquals("Match not found", e.getMessage());
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
        verify(matchRepository).findById(mId);
    }

    @Test
    void getPlayers_Success() throws Exception {
        // Arrange
        Long matchId = (long) 1;
        Match match = new Match();
        match.setId(matchId);
        User player1 = new User();
        player1.setId(matchId);
        player1.setUsername("player1");
        User player2 = new User();
        player2.setId(matchId + 1);
        player2.setUsername("player2");
        match.setPlayer1(matchId);
        match.setPlayer2(matchId + 1);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(userRepository.findById(matchId)).thenReturn(Optional.of(player1));
        when(userRepository.findById(matchId + 1)).thenReturn(Optional.of(player2));

        // Act
        String[] players = matchService.getPlayers(matchId);

        // Assert
        assertArrayEquals(new String[] { "player1", "player2" }, players);
        verify(matchRepository).findById(matchId);
        verify(userRepository).findById(matchId);
        verify(userRepository).findById(matchId + 1);
    }

    @Test
    void testGetPlayers_MatchNotFound() {
        // Arrange
        Long matchId = (long) 1;

        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            matchService.getPlayers(matchId);
        });
        assertEquals("Match not found", exception.getMessage());
        verify(matchRepository).findById(matchId);
        verify(userRepository, never()).findById(matchId);
        verify(userRepository, never()).findById(matchId + 1);
    }

    @Test
    void testGetPlayers_Player1NotFound() {
        // Arrange
        Long matchId = (long) 1;
        Match match = new Match();
        match.setId(matchId);
        User player2 = new User();
        player2.setId(matchId + 1);
        player2.setUsername("player2");
        match.setPlayer1(matchId);
        match.setPlayer2(matchId + 1);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(userRepository.findById(matchId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            matchService.getPlayers(matchId);
        });
        assertEquals("Player 1 not found", exception.getMessage());
        verify(matchRepository).findById(matchId);
        verify(userRepository).findById(matchId);
        verify(userRepository, never()).findById(matchId + 1);
    }

    @Test
    void testGetPlayers_Player2NotFound() {
        // Arrange
        Long matchId = (long) 1;
        Match match = new Match();
        match.setId(matchId);
        User player1 = new User();
        player1.setId(matchId);
        player1.setUsername("player1");
        match.setPlayer1(matchId);
        match.setPlayer2(matchId + 1);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(userRepository.findById(matchId)).thenReturn(Optional.of(player1));
        when(userRepository.findById(matchId + 1)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            matchService.getPlayers(matchId);
        });

        assertEquals("Player 2 not found", exception.getMessage());
        verify(matchRepository).findById(matchId);
        verify(userRepository).findById(matchId);
        verify(userRepository).findById(matchId + 1);
    }
}
