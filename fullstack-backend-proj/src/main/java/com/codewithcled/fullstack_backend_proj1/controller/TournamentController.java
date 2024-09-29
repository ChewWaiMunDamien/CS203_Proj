package com.codewithcled.fullstack_backend_proj1.controller;


import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.service.TournamentService;
import com.codewithcled.fullstack_backend_proj1.service.TournamentServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class TournamentController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentService tournamentService;

    @PostMapping("/tournament")
    public ResponseEntity<Tournament> createTournament(@RequestBody Tournament tournament) {
        Tournament createdTournament =  tournamentRepository.save(tournament);
        return new ResponseEntity<>(createdTournament, HttpStatus.CREATED);  // Return 201 Created with the new tournament
    }

    @GetMapping("/tournaments")
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        List<Tournament> tournaments = tournamentRepository.findAll();
        if (tournaments.isEmpty()) {
            return ResponseEntity.noContent().build();  // Return 204 No Content if the list is empty
        }
        return ResponseEntity.ok(tournaments);  // Return 200 OK with the list of users
    }

    @GetMapping("/tournament/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable("id") Long id) {
        return tournamentRepository.findById(id)
                .map(tournament -> ResponseEntity.ok(tournament))  // Return 200 OK with the user
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());  // Return 404 if user not found
    }

    @PostMapping("/tournament/{id}/round")
    public ResponseEntity<String> addRound(@RequestBody Round round, @PathVariable("id") Long id) throws Exception {
        // Find the tournament by ID
        Tournament currentTournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Tournament not found"));

        // Set the tournament for the round (to maintain the bidirectional relationship)
        round.setTournament(currentTournament);

        // Add the round to the tournament's list of rounds
        currentTournament.getRounds().add(round);

        // Save the updated tournament (with rounds cascading should save the round)
        tournamentRepository.save(currentTournament);

        return ResponseEntity.ok("Round added successfully to the tournament");
    }


    @GetMapping("/tournament/{id}/participant")
    public ResponseEntity<List<Long>> getTournamentParticipants(@PathVariable("id") Long id) {
        try {
            List<Long> participants = tournamentService.getTournamentParticipants(id);

            return ResponseEntity.ok(participants);  // Return 200 OK with the list of participants
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Return 404 if tournament not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/tournament/{id}/participant/add")
    public ResponseEntity<Tournament> updateTournamentParticipant(@RequestParam("user_id") Long userId, @PathVariable("id") Long id) throws Exception {
        try {
            Tournament updatedTournament = tournamentService.updateUserParticipating(userId, id);
            return ResponseEntity.ok(updatedTournament);  // Return 200 OK with the updated tournament
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Return 400 Bad Request for errors
        }
    }

    @PutMapping("/tournament/{id}/participant/delete")
    public ResponseEntity<Tournament> removeParticipant(
            @RequestParam("user_id") Long userId,
            @PathVariable("id") Long id) {
        try {
            Tournament updatedTournament = tournamentService.removeUserParticipating(userId, id);
            return ResponseEntity.ok(updatedTournament);  // Return 200 OK with the updated tournament
        } catch (Exception e) {
            // Log the exception message for debugging
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Return 400 Bad Request for errors
        }
    }

    @DeleteMapping("/tournament/{id}")
    public ResponseEntity<String> deleteTournament(@PathVariable("id") Long id) {
        try {
            tournamentRepository.deleteById(id);
            return ResponseEntity.ok("Tournament with ID " + id + " has been deleted.");  // Return 200 OK with success message
        }  catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the tournament.");  // Return 500 Internal Server Error for other issues
        }
    }

    @PutMapping("/tournament/{id}")
    public ResponseEntity<Tournament> updateTournament(@RequestBody Tournament newTournament, @PathVariable("id") Long id) {
        try {
            Tournament updatedTournament = tournamentService.updateTournament(id, newTournament);
            return ResponseEntity.ok(updatedTournament);  // Return 200 OK with the updated tournament
        } catch (Exception e) {
            // Log the exception message for debugging
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Return 400 Bad Request for errors
        }
    }

}
