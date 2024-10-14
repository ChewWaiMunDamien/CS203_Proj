package com.codewithcled.fullstack_backend_proj1;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.codewithcled.fullstack_backend_proj1.DTO.CreateTournamentRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignUpRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AdminControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken = "adminadmin123";

    private String urlPrefix = "/admin";

    @BeforeEach
    public void setUp() {
        User admin = new User();
        admin.setUsername("AdminUser");
        admin.setEmail("AdminUser");
        admin.setPassword(passwordEncoder.encode("Admin"));
        admin.setRole("ROLE_ADMIN");
        userRepository.save(admin);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    @Test
    public void validateAdminToken_Success() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/signin/validate-admin-token");
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setToken(adminToken);

        ResponseEntity<?> result = restTemplate.withBasicAuth("AdminUser", "Admin")
                .postForEntity(uri, tokenRequest, TokenResponse.class);

        assertEquals(200, result.getStatusCode());
    }

    @Test
    public void validateAdminToken_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/signin/validate-admin-token");
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setToken("fa");

        ResponseEntity<?> result = restTemplate.withBasicAuth("AdminUser", "Admin")
                .postForEntity(uri, tokenRequest, TokenResponse.class);

        assertEquals(401, result.getStatusCode());
    }

    @Test
    public void createdTournament_Success() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/tournament");
        CreateTournamentRequest createTournamentRequest = new CreateTournamentRequest();
        createTournamentRequest.setCurrentSize(0);
        createTournamentRequest.setDate("10/10/2023");
        createTournamentRequest.setNoOfRounds(4);
        createTournamentRequest.setSize(0);
        createTournamentRequest.setStatus("Active");
        createTournamentRequest.setTournament_name("TestTournament");

        ResponseEntity<TournamentDTO> result = restTemplate.withBasicAuth("AdminUser", "Admin")
                .postForEntity(uri, createTournamentRequest,
                        TournamentDTO.class);

        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("TestTournament", result.getBody().getTournamentName());

    }

    @Test
    public void createdTournament_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/tournament");

        ResponseEntity<TournamentDTO> result = restTemplate.withBasicAuth("AdminUser", "Admin")
                .postForEntity(uri, null, TournamentDTO.class);

        assertEquals(400, result.getStatusCode());
    }

    @Test
    public void createUser_Success() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/signup/user");
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setElo((double) 100);
        signUpRequest.setEmail("TestUser");
        signUpRequest.setPassword(passwordEncoder.encode("TestUser"));
        signUpRequest.setRole("ROLE_USER");
        signUpRequest.setUsername("TestUser");

        ResponseEntity<AuthResponse> result = restTemplate.withBasicAuth("AdminUser", "Admin")
                .postForEntity(uri, signUpRequest, AuthResponse.class);

        assertEquals(201, result.getStatusCode());
    }

    @Test
    public void createUser_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/signup/user");
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setElo((double) 100);
        signUpRequest.setEmail("TestUser");
        signUpRequest.setPassword(passwordEncoder.encode("TestUser"));
        signUpRequest.setRole("ROLE_USER");
        signUpRequest.setUsername("TestUser");

        User originalUser = new User();
        originalUser.setElo((double) 100);
        originalUser.setEmail("TestUser");
        originalUser.setPassword(passwordEncoder.encode("TestUser"));
        originalUser.setRole("ROLE_USER");
        originalUser.setUsername("TestUser");
        userRepository.save(originalUser);

        ResponseEntity<AuthResponse> result = restTemplate.withBasicAuth("AdminUser", "Admin")
                .postForEntity(uri, signUpRequest, AuthResponse.class);

        assertEquals(409, result.getStatusCode());
    }

    @Test
    public void deleteUser_Success() throws Exception {
        User originalUser = new User();
        originalUser.setElo((double) 100);
        originalUser.setEmail("TestUser");
        originalUser.setPassword(passwordEncoder.encode("TestUser"));
        originalUser.setRole("ROLE_USER");
        originalUser.setUsername("TestUser");
        User savedUser = userRepository.save(originalUser);

        URI uri = new URI(baseUrl + port + urlPrefix + "/" + savedUser.getId());

        ResponseEntity<Void> result = restTemplate.withBasicAuth("AdminUser", "Admin")
                .exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(204, result.getStatusCode());
    }

    @Test
    public void deleteUser_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/" + (long) 110);

        ResponseEntity<Void> result = restTemplate.withBasicAuth("AdminUser", "Admin")
                .exchange(uri, HttpMethod.DELETE, null, void.class);

        assertEquals(404, result.getStatusCode());
    }

    @Test
    public void getUserByUsername_Success() throws Exception {
        User originalUser = new User();
        originalUser.setElo((double) 100);
        originalUser.setEmail("TestUser");
        originalUser.setPassword(passwordEncoder.encode("TestUser"));
        originalUser.setRole("ROLE_USER");
        originalUser.setUsername("TestUser");
        userRepository.save(originalUser);

        URI uri = new URI(baseUrl + port + urlPrefix + "/TestUser");

        ResponseEntity<UserDTO> result = restTemplate.withBasicAuth("AdminUser", "Admin")
                .getForEntity(uri, UserDTO.class);

        assertEquals(200, result.getStatusCode());
    }

    @Test
    public void getUserByUsername_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/TestUser");

        ResponseEntity<UserDTO> result = restTemplate.withBasicAuth("AdminUser", "Admin")
                .getForEntity(uri, UserDTO.class);

        assertEquals(404, result.getStatusCode());
    }

    @Test
    public void deleteTournament_Success() throws Exception {
        Long tId = (long) 110;

        Tournament originalTournament = new Tournament();
        originalTournament.setSize(0);
        originalTournament.setDate("20/10/2002");
        originalTournament.setId(tId);
        originalTournament.setNoOfRounds(1);
        originalTournament.setTournament_name("TestUser");
        tournamentRepository.save(originalTournament);

        URI uri = new URI(baseUrl + port + urlPrefix + "/tournament/" + tId);

        ResponseEntity<String> result = restTemplate.withBasicAuth("AdminUser", "Admin")
                .exchange(uri, HttpMethod.DELETE, null, String.class);

        assertEquals(200, result.getStatusCode());
        assertEquals("Tournament with ID " + tId + " has been deleted.", result.getBody());
    }

    @Test
    public void deleteTournament_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/tournament/" + 110);

        ResponseEntity<String> result = restTemplate.withBasicAuth("AdminUser", "Admin")
                .exchange(uri, HttpMethod.DELETE, null, String.class);

        assertEquals(500, result.getStatusCode());
        assertEquals("An error occurred while deleting the tournament.", result.getBody());
    }

    @Test
    public void updateUser_Success() throws Exception {
        User originalUser = new User();
        originalUser.setElo((double) 100);
        originalUser.setEmail("TestUser");
        originalUser.setPassword(passwordEncoder.encode("TestUser"));
        originalUser.setRole("ROLE_USER");
        originalUser.setUsername("TestUser");
        User savedUser = userRepository.save(originalUser);

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setElo((double) 100);
        signUpRequest.setEmail("TestUser");
        signUpRequest.setPassword(passwordEncoder.encode("TestUser"));
        signUpRequest.setRole("ROLE_USER");
        signUpRequest.setUsername("updatedUser");

        URI uri = new URI(baseUrl + port + urlPrefix + "/user/" + savedUser.getId());
        HttpEntity<SignUpRequest> updateUser = new HttpEntity<>(signUpRequest);

        ResponseEntity<UserDTO> result = restTemplate.withBasicAuth("AdminUser", "Admin")
                .exchange(uri, HttpMethod.PUT, updateUser, UserDTO.class);

        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("updatedUser", result.getBody().getUsername());
    }

    @Test
    public void updateUser_Failure() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setElo((double) 100);
        signUpRequest.setEmail("TestUser");
        signUpRequest.setPassword(passwordEncoder.encode("TestUser"));
        signUpRequest.setRole("ROLE_USER");
        signUpRequest.setUsername("updatedUser");

        URI uri = new URI(baseUrl + port + urlPrefix + "/user/9");
        HttpEntity<SignUpRequest> updateUser = new HttpEntity<>(signUpRequest);

        ResponseEntity<UserDTO> result = restTemplate.withBasicAuth("AdminUser", "Admin")
                .exchange(uri, HttpMethod.PUT, updateUser, UserDTO.class);

        assertEquals(200, result.getStatusCode());
        assertEquals("updatedUser", result.getBody().getUsername());
    }

    public static class TokenRequest {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class TokenResponse {
        private boolean valid;

        public TokenResponse(boolean valid) {
            this.valid = valid;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }
    }
}