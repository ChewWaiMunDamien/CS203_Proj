package com.codewithcled.fullstack_backend_proj1.IntegrationTests;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.util.UriComponentsBuilder;

import com.codewithcled.fullstack_backend_proj1.DTO.CreateTournamentRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.repository.RoundRepository;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TournamentControllerIntegrationTest {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    private String urlPrefix = "/t";

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        tournamentRepository.deleteAll();
        roundRepository.deleteAll();
        matchRepository.deleteAll();
    }

    @Test
    public void getAllTournaments_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");
        tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("testTournament", result.getBody().get(0).getTournamentName());
    }

    @Test
    public void getAllTournaments_Failure() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    public void getTournamentById_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setDate("10/20/1203");
        tournament.setStatus("active");
        Tournament savedTournament = tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedTournament.getId());

        ResponseEntity<TournamentDTO> result = restTemplate.getForEntity(url, TournamentDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("testTournament", result.getBody().getTournamentName());
    }

    @Test
    public void getTournamentById_Failure() throws Exception {

        URI url = new URI(baseUrl + port + urlPrefix + "/2423");

        ResponseEntity<TournamentDTO> result = restTemplate.getForEntity(url, TournamentDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    //@Test // Issue with sending the post getting unsupported Media Type Exception
    public void addRound_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(2);
        tournament.setNoOfRounds(3);
        tournament.setDate("10/20/1203");
        tournament.setStatus("active");
        tournament.setRounds(new ArrayList<>());

        // Save the tournament to the repository
        Tournament savedTournament = tournamentRepository.save(tournament);

        // Create a Round object
        Round round = new Round();
        round.setRoundNum(1);
        round.setId((long) 138021); // Ensure this ID is appropriate based on your application's logic

        // Initialize scoreboard and matchList if necessary
        Map<Long, Double> scoreboard = new HashMap<>();
        round.setScoreboard(scoreboard);
        round.setMatchList(new ArrayList<>()); // Assuming matches are empty for now

        // Create the URL
        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/" + savedTournament.getId() + "/round");

        // Execute the POST request
        ResponseEntity<String> result = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(round),
                String.class);

        // Assertions
        assertEquals("Round added successfully to the tournament", result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    //@Test
    public void addRound_Failure() {

        Round round = new Round();
        round.setRoundNum(1);
        round.setId(134L);
        round.setScoreboard(new HashMap<>());
        round.setMatchList(new ArrayList<>());
        boolean exceptionThrown = false;

        try {

            URI url = new URI(baseUrl + port + urlPrefix + "/tournament/204830/round");

            restTemplate.postForEntity(url, round, String.class);
        } catch (Exception e) {
            assertEquals("Tournament not found", e.getMessage());
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);

    }

    @Test
    public void getTournamentParticipants_Success() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");

        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournamentList.add(tournament);
        userList.add(user);

        user.setCurrentTournaments(tournamentList);
        tournament.setParticipants(userList);

        Tournament savedTournament = tournamentRepository.save(tournament);
        userRepository.save(user);

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedTournament.getId() + "/participant");

        ResponseEntity<List<UserDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("testUser", result.getBody().get(0).getUsername());
    }

    @Test
    public void getTournamentParticipants_Failure() throws Exception {
        long tId = (long) 110;

        URI url = new URI(baseUrl + port + urlPrefix + "/" + tId + "/participant");

        ResponseEntity<List<UserDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDTO>>() {
                });

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void updateTournamentParticipant_Success() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");

        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(5);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournament.setParticipants(userList);
        user.setCurrentTournaments(tournamentList);

        User savedUser = userRepository.save(user);
        Tournament savedTournament = tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedTournament.getId() + "/participant/add");
        String urlTemplate = UriComponentsBuilder.fromUri(url)
                .queryParam("user_id", "{user_id}")
                .encode()
                .toUriString();

        Map<String, Long> params = new HashMap<>();
        params.put("user_id", savedUser.getId());

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                urlTemplate,
                HttpMethod.PUT,
                null,
                TournamentDTO.class,
                params);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("testUser", result.getBody().getParticipants().get(0).getUsername());
    }

    @Test
    public void updateTournamentParticipant_Failure() throws Exception {

        URI url = new URI(baseUrl + port + urlPrefix + "/234/participant/add");
        String urlTemplate = UriComponentsBuilder.fromUri(url)
                .queryParam("user_id", "{user_id}")
                .encode()
                .toUriString();

        Map<String, Long> params = new HashMap<>();
        params.put("user_id", (long) 600);

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                urlTemplate,
                HttpMethod.PUT,
                null,
                TournamentDTO.class,
                params);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void getTournamentWithNoCurrentUser_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");
        tournamentRepository.save(tournament);

        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");
        User savedUser = userRepository.save(user);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/" + savedUser.getId());

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("testTournament", result.getBody().get(0).getTournamentName());
    }

    @Test
    public void getTournamentWithNoCurrentUser_Failure() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");
        tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/12343");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void getUsersWithNoCurrentTournament_Success() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");
        user.setPassword(passwordEncoder.encode("testUser"));
        user.setElo((double) 100);
        user.setCurrentTournaments(new ArrayList<>());
        User savedUser = userRepository.save(user);

        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");
        tournament.setParticipants(new ArrayList<>());
        tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/" + savedUser.getId());

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("testTournament", result.getBody().get(0).getTournamentName());
    }

    @Test
    public void getUsersWithNoCurrentTournament_Failure() throws Exception {

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/12380");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void getUsersWithNoCurrentTournament_FailureNoUsers() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");
        tournament.setParticipants(new ArrayList<>());
        Tournament saveTournament = tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/" + saveTournament.getId());

        ResponseEntity<List<UserDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDTO>>() {
                });

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void removeParticipant_Success() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");

        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();

        Tournament savedTournament = tournamentRepository.save(tournament);
        User savedUser = userRepository.save(user);

        tournamentList.add(savedTournament);
        userList.add(savedUser);

        savedUser.setCurrentTournaments(tournamentList);
        savedTournament.setParticipants(userList);

        savedTournament = tournamentRepository.save(savedTournament);
        savedUser = userRepository.save(savedUser);

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedTournament.getId() + "/participant/delete");
        String urlTemplate = UriComponentsBuilder.fromUri(url)
                .queryParam("user_id", "{user_id}")
                .encode()
                .toUriString();

        Map<String, Long> params = new HashMap<>();
        params.put("user_id", savedUser.getId());

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                urlTemplate,
                HttpMethod.PUT,
                null,
                TournamentDTO.class,
                params);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertIterableEquals(new ArrayList<UserDTO>(), result.getBody().getParticipants());
    }

    @Test
    public void removeParticipant_Failure() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/132/participant/delete");
        String urlTemplate = UriComponentsBuilder.fromUri(url)
                .queryParam("user_id", "{user_id}")
                .encode()
                .toUriString();

        Map<String, Long> params = new HashMap<>();
        params.put("user_id", (long) 13028);

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(urlTemplate, HttpMethod.PUT, null,
                TournamentDTO.class, params);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void deleteTournament_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");
        Tournament savedTournament = tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/" + savedTournament.getId());

        ResponseEntity<String> result = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Tournament with ID " + savedTournament.getId() + " has been deleted.", result.getBody());
        assertEquals(0, tournamentRepository.count());
    }

    @Test
    public void deleteTournament_Failure() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/1183");

        ResponseEntity<String> result = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Tournament with ID " + 1183 + " not found.", result.getBody());
    }

    @Test
    public void updateTournament_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");
        Tournament savedTournament = tournamentRepository.save(tournament);

        CreateTournamentRequest updateTournament = new CreateTournamentRequest();
        updateTournament.setTournament_name("newTournament");

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedTournament.getId());

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(updateTournament),
                TournamentDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("newTournament", result.getBody().getTournamentName());
    }

    @Test
    public void updateTournament_Failure() throws Exception {
        CreateTournamentRequest updateTournament = new CreateTournamentRequest();
        updateTournament.setTournament_name("newTournament");

        URI url = new URI(baseUrl + port + urlPrefix + "/1408402");

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(updateTournament),
                TournamentDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void startTournament_Success() throws Exception {
        Tournament testTournament = new Tournament();
        User player1 = new User();
        User player2 = new User();
        Double elo = (double) 1000;
        String userRole = "ROLE_USER";
        String userName = "testUser";
        String tournamentStatus = "active";
        int tournamentSize = 2;

        player1.setUsername(userName + 1);
        player1.setRole(userRole);
        player1.setEmail(userName + 1);
        player1.setElo(elo);

        player2.setUsername(userName + 1);
        player2.setRole(userRole);
        player2.setEmail(userName + 2);
        player2.setElo(elo);

        testTournament.setTournament_name("testTournament");
        testTournament.setSize(tournamentSize);
        testTournament.setCurrentSize(tournamentSize);
        testTournament.setNoOfRounds(1);
        testTournament.setStatus(tournamentStatus);
        testTournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournamentList.add(testTournament);
        userList.add(player1);
        userList.add(player2);

        testTournament.setRounds(new ArrayList<Round>());
        player1.setCurrentTournaments(tournamentList);
        player2.setCurrentTournaments(tournamentList);
        testTournament.setParticipants(userList);

        Tournament savedTournament = tournamentRepository.save(testTournament);
        userRepository.save(player1);
        userRepository.save(player2);

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedTournament.getId() + "/start");

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                TournamentDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("ongoing", result.getBody().getStatus());
    }

    @Test
    void startTournament_Failure_CannotFindTournament() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/404/start");

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                TournamentDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void checkTournamentComplete_Success() throws Exception {
        Tournament testTournament = new Tournament();
        User player1 = new User();
        User player2 = new User();
        Round testRound = new Round();
        Match testMatch = new Match();
        Double elo = (double) 1000;
        String userRole = "ROLE_USER";
        String userName = "testUser";
        String tournamentStatus = "active";
        int tournamentSize = 2;

        player1.setUsername(userName + 1);
        player1.setRole(userRole);
        player1.setEmail(userName + 1);
        player1.setElo(elo);

        player2.setUsername(userName + 1);
        player2.setRole(userRole);
        player2.setEmail(userName + 2);
        player2.setElo(elo);

        testTournament.setTournament_name("testTournament");
        testTournament.setSize(tournamentSize);
        testTournament.setCurrentSize(tournamentSize);
        testTournament.setNoOfRounds(1);
        testTournament.setStatus(tournamentStatus);
        testTournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournamentList.add(testTournament);
        userList.add(player1);
        userList.add(player2);

        player1.setCurrentTournaments(tournamentList);
        player2.setCurrentTournaments(tournamentList);
        testTournament.setParticipants(userList);

        Tournament savedTournament = tournamentRepository.save(testTournament);
        User savedPlayer1 = userRepository.save(player1);
        User savedPlayer2 = userRepository.save(player2);

        testRound.setRoundNum(1);
        testRound.setTournament(savedTournament);
        testRound.setScoreboard(new HashMap<>());

        Round savedRound = roundRepository.save(testRound);

        testMatch.setPlayer1(savedPlayer1.getId());
        testMatch.setPlayer2(savedPlayer2.getId());
        testMatch.setIsComplete(false);
        testMatch.setRound(savedRound);
        savedRound.setMatchList(List.of(testMatch));

        roundRepository.save(savedRound);

        matchRepository.save(testMatch);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/" + savedTournament.getId()
                + "/checkComplete");

        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Successfully checked tournamentService.isComplete", result.getBody());
    }
}